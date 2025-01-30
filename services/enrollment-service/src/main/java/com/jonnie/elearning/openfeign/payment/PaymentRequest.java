package com.jonnie.elearning.openfeign.payment;

import com.jonnie.elearning.utils.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String userId,
        String cartId,
        List<String> courseIds,
        List<String> instructorId
) {
}
