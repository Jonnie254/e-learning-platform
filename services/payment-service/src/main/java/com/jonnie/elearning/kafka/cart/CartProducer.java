package com.jonnie.elearning.kafka.cart;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartProducer {
    private final KafkaTemplate<String, CartNotification> kafkaTemplate;
    public void cartConfirmationStatus(CartNotification cartNotification){
        log.info("Sending cart confirmation: {}", cartNotification);
        kafkaTemplate.send("cart-status-topic", cartNotification);
    }

}
