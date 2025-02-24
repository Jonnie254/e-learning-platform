package com.jonnie.elearning.payment;

import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.config.paypal.PayPalClient;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.kafka.payment.PaymentCompletedEvent;
import com.jonnie.elearning.openfeign.courses.CourseClient;
import com.jonnie.elearning.openfeign.courses.CourseDetailsResponse;
import com.jonnie.elearning.payment.responses.CourseEarningResponse;
import com.jonnie.elearning.payment.responses.TotalRevenueStatsResponse;
import com.jonnie.elearning.utils.PaymentMethod;
import com.paypal.api.payments.*;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final APIContext apiContext;
    private final PayPalClient payPalClient;
    private final ApplicationEventPublisher eventPublisher;
    private final CourseClient courseClient;
    private final MongoTemplate mongoTemplate;

    @Value("${application.paypal-url.cancel-url}")
    private String cancelUrl;

    @Value("${application.paypal-url.success-url}")
    private String successUrl;

    @Transactional
    public String createPayPalPayment(PaymentRequest paymentRequest) {
        log.info("Received PayPal payment request for userId: {}", paymentRequest.userId());
        Payment payment = getPayment(paymentRequest);
        try {
            Payment createdPayment = payment.create(apiContext);
            log.info("PayPal payment created successfully. Payment ID: {}", createdPayment.getId());

            // Create a PaymentRecord and store transaction details
            PaymentRecord paymentRecord = PaymentRecord.builder()
                    .paymentId(createdPayment.getId())
                    .userId(paymentRequest.userId())
                    .cartId(paymentRequest.cartId())
                    .cartReference(paymentRequest.cartReference())
                    .paymentMethod(PaymentMethod.PAYPAL)
                    .isPaid(false)
                    .amount(paymentRequest.amount())
                    .customerFirstName(paymentRequest.customerFirstName())
                    .customerLastName(paymentRequest.customerLastName())
                    .customerEmail(paymentRequest.customerEmail())
                    .coursePaymentDetails(paymentRequest.coursePaymentDetails())
                    .build();
            paymentRepository.save(paymentRecord);
            log.info("PaymentRecord saved successfully for Payment ID: {}", paymentRecord.getPaymentId());

            // Get PayPal approval URL
            return createdPayment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .findFirst()
                    .map(Links::getHref)
                    .orElseThrow(() -> new BusinessException("No approval_url found for PayPal Payment ID: " + createdPayment.getId()));

        } catch (PayPalRESTException e) {
            log.error("PayPalRESTException while creating payment. Error: {}", e.getMessage(), e);
            throw new BusinessException("Error creating PayPal payment: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while creating PayPal payment. Error: {}", e.getMessage(), e);
            throw new BusinessException("Unexpected error occurred: " + e.getMessage());
        }
    }

    private Payment getPayment(PaymentRequest paymentRequest) {
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(paymentRequest.amount()));

        Transaction transaction = new Transaction();
        transaction.setDescription("Payment for eLearning courses");
        transaction.setAmount(amount);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(List.of(transaction));
        payment.setRedirectUrls(redirectUrls);
        return payment;
    }

    @Transactional
    public boolean completePayPalPayment(String paymentId, String token, String payerId) {
        log.info("Completing PayPal payment. Payment ID: {}, Payer ID: {}", paymentId, payerId);

        try {
            Payment executedPayment = payPalClient.executePayment(paymentId, payerId);

            if ("approved".equals(executedPayment.getState())) {
                PaymentRecord paymentRecord = paymentRepository.findById(paymentId)
                        .orElseThrow(() -> new BusinessException("Payment record not found for Payment ID: " + paymentId));

                paymentRecord.setPaid(true);
                paymentRepository.save(paymentRecord);
                log.info("Payment completed successfully. Payment ID: {}", paymentId);

                eventPublisher.publishEvent(new PaymentCompletedEvent(this, paymentRecord));
                return true;
            } else {
                log.warn("Payment not approved. Payment ID: {}", paymentId);
                return false;
            }
        } catch (PayPalRESTException e) {
            log.error("PayPalRESTException while executing payment. Payment ID: {}. Error: {}", paymentId, e.getMessage(), e);
            throw new BusinessException("Error executing PayPal payment: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while executing payment. Payment ID: {}. Error: {}", paymentId, e.getMessage(), e);
            throw new BusinessException("Unexpected error executing payment: " + e.getMessage());
        }
    }

    public TotalRevenueStatsResponse getInstructorTotalEarnings(String instructorId) {
        Query query = Query.query(
                Criteria.where("isPaid").is(true)
                        .and("coursePaymentDetails.instructorId").is(instructorId)
        );
        List<PaymentRecord> payments = mongoTemplate.find(query, PaymentRecord.class);

        if (payments.isEmpty()) {
            return new TotalRevenueStatsResponse(BigDecimal.ZERO);
        }
        BigDecimal totalEarnings = payments.stream()
                .flatMap(payment -> payment.getCoursePaymentDetails().stream())
                .filter(details -> details.instructorId().equals(instructorId))
                .map(details -> new BigDecimal(String.valueOf(details.price())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TotalRevenueStatsResponse(totalEarnings);
    }

    public TotalRevenueStatsResponse getAdminTotalEarnings() {
        List<PaymentRecord> payments = paymentRepository.findAllByIsPaid(true);
        if (payments.isEmpty()) {
            return new TotalRevenueStatsResponse(BigDecimal.ZERO);
        }
       BigDecimal totalEarnings = payments.stream()
               .map(PaymentRecord::getAmount)
               .filter(Objects::nonNull)
               .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TotalRevenueStatsResponse(totalEarnings);
    }

    public PageResponse<CourseEarningResponse> getInstructorRevenueSummary(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Query query = Query.query(
                Criteria.where("isPaid").is(true)
                        .and("coursePaymentDetails.instructorId").is(userId)
        );
       long totalElements = mongoTemplate.count(query, PaymentRecord.class);
       List<PaymentRecord> payments = mongoTemplate.find(query.with(pageable), PaymentRecord.class);
        if (payments.isEmpty()) {
            return new PageResponse<>(List.of(), page, size, totalElements, 0, true, true);
        }
        Map<String, BigDecimal> earningsPerCourse = payments.stream()
                .flatMap(payment -> payment.getCoursePaymentDetails().stream())
                .filter(details -> details.instructorId().equals(userId))
                .collect(Collectors.groupingBy(
                        CoursePaymentDetails::courseId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                CoursePaymentDetails::price,
                                BigDecimal::add
                        )
                ));
        return getCourseEarningResponsePageResponse(page, size, totalElements, earningsPerCourse);
    }

    public PageResponse<CourseEarningResponse> getAdminRevenueSummary(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Query query = Query.query(Criteria.where("isPaid").is(true));
        var totalElements = mongoTemplate.count(query, PaymentRecord.class);

        List<PaymentRecord> payments = mongoTemplate.find(query.with(pageable), PaymentRecord.class);

        if (payments.isEmpty()) {
                 return new PageResponse<>(List.of(), page, size, totalElements, 0, true, true);
        }

        // Check if coursePaymentDetails exist
        payments.forEach(payment ->
                log.info("Admin Payment details: {}", payment.getCoursePaymentDetails()));

        Map<String, BigDecimal> earningsPerCourse = payments.stream()
                .flatMap(payment -> {
                    if (payment.getCoursePaymentDetails() == null || payment.getCoursePaymentDetails().isEmpty()) {
                        return Stream.empty();
                    }
                    return payment.getCoursePaymentDetails().stream();
                })
                .filter(details -> details.courseId() != null)
                .collect(Collectors.groupingBy(
                        CoursePaymentDetails::courseId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                CoursePaymentDetails::price,
                                BigDecimal::add
                        )
                ));
        if (earningsPerCourse.isEmpty()) {
            return new PageResponse<>(List.of(), page, size, totalElements, 0, true, true);
        }
        return getCourseEarningResponsePageResponse(page, size, totalElements, earningsPerCourse);
    }


    private PageResponse<CourseEarningResponse> getCourseEarningResponsePageResponse(int page, int size, long totalElements,
                                                                                     Map<String, BigDecimal> earningsPerCourse) {
        List<CourseEarningResponse> courseEarningResponses = earningsPerCourse.entrySet().stream()
                .map(entry -> {
                    CourseDetailsResponse courseDetails = courseClient.getCourseDetails(entry.getKey());
                    return CourseEarningResponse.builder()
                            .courseDetailsResponse(courseDetails)
                            .totalEarning(entry.getValue())
                            .build();
                })
                .toList();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(courseEarningResponses,
                page, size,
                totalElements,
                totalPages,
                page == totalPages - 1,
                page == 0);
    }
}