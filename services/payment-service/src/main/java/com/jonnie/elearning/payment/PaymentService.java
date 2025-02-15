package com.jonnie.elearning.payment;

import com.jonnie.elearning.config.paypal.PayPalClient;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.kafka.cart.CartNotification;
import com.jonnie.elearning.kafka.cart.CartProducer;
import com.jonnie.elearning.kafka.enrollment.EnrollmentConfirmation;
import com.jonnie.elearning.kafka.enrollment.EnrollmentProducer;
import com.jonnie.elearning.kafka.notification.NotificationProducer;
import com.jonnie.elearning.kafka.notification.PaymentNotificationRequest;
import com.jonnie.elearning.kafka.payment.PaymentCompletedEvent;
import com.jonnie.elearning.utils.CartStatus;
import com.jonnie.elearning.utils.PaymentMethod;
import com.paypal.api.payments.*;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final APIContext apiContext;
    private final PayPalClient payPalClient;
    private final EnrollmentProducer enrollmentProducer;
    private final NotificationProducer notificationProducer;
    private final CartProducer cartProducer;
    private final ApplicationEventPublisher eventPublisher;
    @Value("${application.paypal-url.cancel-url}")
    private String cancelUrl;
    @Value("${application.paypal-url.success-url}")
    private String successUrl;
    public String createPayPalPayment(PaymentRequest paymentRequest) {
        log.info("Received payment request: {}", paymentRequest);
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(paymentRequest.amount()));

        Transaction transaction = new Transaction();
        transaction.setDescription("Payment for eLearning courses");
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);
        try {
            Payment createdPayment = payment.create(apiContext);
            PaymentRecord paymentRecord = PaymentRecord.builder()
                    .paymentId(createdPayment.getId())
                    .userId(paymentRequest.userId())
                    .courseIds(paymentRequest.courseIds())
                    .instructorIds(paymentRequest.instructorId())
                    .paymentMethod(PaymentMethod.PAYPAL)
                    .isPaid(false)
                    .cartReference(paymentRequest.cartReference())
                    .amount(paymentRequest.amount())
                    .customerFirstName(paymentRequest.customerFirstName())
                    .customerLastName(paymentRequest.customerLastName())
                    .customerEmail(paymentRequest.customerEmail())
                    .build();
            paymentRepository.save(paymentRecord);
            return createdPayment.getLinks().stream()
                    .filter(link -> link.getRel().equals("approval_url"))
                    .findFirst()
                    .map(Links::getHref)
                    .orElseThrow(() -> new BusinessException("No approval_url found"));
        } catch (Exception e) {
            throw new BusinessException("Payment processing failed");
        }
    }

    @Transactional
    public boolean completePayPalPayment(String paymentId, String token, String payerId) {
        log.info("Starting PayPal payment execution. Payment ID: {}, Payer ID: {}", paymentId, payerId);
        try {
            Payment executedPayment = payPalClient.executePayment(paymentId, payerId);
            log.info("Payment execution response received. State: {}", executedPayment.getState());

            if ("approved".equals(executedPayment.getState())) {
                PaymentRecord paymentRecord = paymentRepository.findById(paymentId)
                        .orElseThrow(() -> new BusinessException("Payment record not found for ID: " + paymentId));

                log.info("Payment record found. Marking as paid. Payment ID: {}", paymentId);
                paymentRecord.setPaid(true);
                paymentRepository.save(paymentRecord);
                log.info("Payment record updated successfully. Payment ID: {}", paymentId);
                log.info("Publishing the event. Payment ID: {}", paymentId);
                eventPublisher.publishEvent(new PaymentCompletedEvent(this, paymentRecord));
                log.info("PayPal payment process completed successfully. Payment ID: {}", paymentId);
                return true;
            } else {
                log.warn("Payment execution failed or not approved. Payment ID: {}, State: {}", paymentId, executedPayment.getState());
                return false;
            }
        } catch (PayPalRESTException e) {
            log.error("PayPalRESTException occurred while executing payment. Payment ID: {}. Error: {}", paymentId, e.getMessage(), e);
            throw new BusinessException("Error executing payment with PayPal: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while executing payment. Payment ID: {}. Error: {}", paymentId, e.getMessage(), e);
            throw new BusinessException("Unexpected error executing payment: " + e.getMessage());
        }
    }


}