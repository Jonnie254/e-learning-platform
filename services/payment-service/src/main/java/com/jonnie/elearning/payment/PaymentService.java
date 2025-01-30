package com.jonnie.elearning.payment;

import com.jonnie.elearning.config.PayPalClient;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.utils.PaymentMethod;
import com.paypal.api.payments.*;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final APIContext apiContext;
    private final PayPalClient payPalClient;
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
                    .instructorIds(paymentRequest.instructorIds())
                    .paymentMethod(PaymentMethod.PAYPAL)
                    .isPaid(false)
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
        try {
            Payment executedPayment = payPalClient.executePayment(paymentId, payerId);

            if ("approved".equals(executedPayment.getState())) {
                PaymentRecord paymentRecord = paymentRepository.findById(paymentId)
                        .orElseThrow(() -> new BusinessException("Payment record not found"));
                paymentRecord.setPaid(true);
                paymentRepository.save(paymentRecord);

                return true;
            } else {
                return false;
            }

        } catch (PayPalRESTException e) {
            throw new BusinessException("Error executing payment with PayPal: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("Unexpected error executing payment: " + e.getMessage());
        }
    }


}

