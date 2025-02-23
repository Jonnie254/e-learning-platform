package com.jonnie.elearning.payment;

import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.config.paypal.PayPalClient;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.kafka.payment.PaymentCompletedEvent;
import com.jonnie.elearning.openfeign.courses.CourseClient;
import com.jonnie.elearning.openfeign.courses.CourseDetailsResponse;
import com.jonnie.elearning.payment.responses.CourseEarningResponse;
import com.jonnie.elearning.payment.responses.InstructorEarningResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public PageResponse<InstructorEarningResponse> getInstructorTotalEarnings(String instructorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Query query = Query.query(
                Criteria.where("isPaid").is(true)
                        .and("coursePaymentDetails.instructorId").is(instructorId)
        );
        List<PaymentRecord> payments = mongoTemplate.find(query, PaymentRecord.class);
        if (payments.isEmpty()) {
            return new PageResponse<>(List.of(), page, size, 0, 0, true, true);
        }
        Map<String, BigDecimal> earningsByCourse = payments.stream()
                .flatMap(payment -> payment.getCoursePaymentDetails().stream())
                .filter(details -> details.instructorId().equals(instructorId))
                .collect(Collectors.groupingBy(
                        CoursePaymentDetails::courseId,
                        Collectors.mapping(
                                details -> new BigDecimal(String.valueOf(details.price())),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
        List<String> courseIds = new ArrayList<>(earningsByCourse.keySet());
        List<CourseDetailsResponse> courseDetails = courseClient.getCoursesByIds(courseIds);
        List<CourseEarningResponse> courseEarnings = courseDetails.stream()
                .map(course -> new CourseEarningResponse(
                        course,
                        earningsByCourse.getOrDefault(course.getCourseId(), BigDecimal.ZERO)
                ))
                .toList();
        BigDecimal totalEarnings = courseEarnings.stream()
                .map(CourseEarningResponse::getTotalEarning)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalElements = courseEarnings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean last = page >= totalPages - 1;
        boolean first = page == 0;
        List<CourseEarningResponse> paginatedEarnings = courseEarnings.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();
        return new PageResponse<>(
                List.of(new InstructorEarningResponse(totalEarnings, paginatedEarnings)),
                page, size, totalElements, totalPages, last, first
        );
    }

}