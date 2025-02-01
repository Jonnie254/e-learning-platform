package com.jonnie.elearning.kafka.notification;

import com.jonnie.elearning.utils.PaymentMethod;

import java.math.BigDecimal;

public record PaymentNotificationRequest(
        String cartReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String customerFirstName,
        String customerLastName,
        String customerEmail
) {

}
