package com.jonnie.elearning.kafka.cart;

import java.math.BigDecimal;

public record CartItemNotifyResponse(
        String courseName,
        BigDecimal coursePrice
) {
}
