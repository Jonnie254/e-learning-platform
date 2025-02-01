package com.jonnie.elearning.kafka.cart;


import java.math.BigDecimal;
import java.util.List;

public record CartConfirmation(
        String cartReference,
        BigDecimal totalAmount,
        UserResponse userResponse,
        List<CartItemNotifyResponse> cartItems
) {
}
