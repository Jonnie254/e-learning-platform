package com.jonnie.elearning.kafka.Payment;

import com.jonnie.elearning.utils.PaymentMethod;

import java.math.BigDecimal;

public record PaymentConfirmation(
        String cartReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String customerFirstName,
        String customerLastName,
        String customerEmail
) {
}
