package com.jonnie.elearning.kafka.cart;

import com.jonnie.elearning.utils.CartStatus;

public record CartNotification(
        String cartReference,
        CartStatus cartStatus
) {
}
