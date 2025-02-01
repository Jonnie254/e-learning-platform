package com.jonnie.elearning.openfeign.payment;

import com.jonnie.elearning.utils.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String cartReference,
        String userId,
        String cartId,
        String customerFirstName,
        String customerLastName,
        String customerEmail,
        List<String> courseIds,
        List<String> instructorId
) {
}
