package com.jonnie.elearning.kafka.cart;

import com.jonnie.elearning.cart.CartService;
import com.jonnie.elearning.utils.CartStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartNotificationConsumer {
    private final CartService cartService;
    @KafkaListener(topics = "cart-status-topic", groupId = "cart-group")
    public void consumeCartPaymentStatus(CartNotification cartNotification) {
        log.info("Consumed cart notification: {}", cartNotification);
        if(cartNotification.cartStatus() == CartStatus.CHECKED_OUT){
            log.info("Handling payment success for cart: {}", cartNotification.cartReference());
            cartService.handlePaymentSuccess(cartNotification.cartReference());
        } else{
            log.info("Handling payment failure for cart: {}", cartNotification.cartReference());
            cartService.handlePaymentFailure(cartNotification.cartReference());
        }

    }
}
