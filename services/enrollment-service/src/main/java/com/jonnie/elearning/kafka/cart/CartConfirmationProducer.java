package com.jonnie.elearning.kafka.cart;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CartConfirmationProducer {
    private final KafkaTemplate<String, CartConfirmation> kafkaTemplate;
    public void sendCartConfirmation(CartConfirmation cartConfirmation) {
     log.info("Sending cart confirmation: {}", cartConfirmation);
     Message<CartConfirmation> message = MessageBuilder
             .withPayload(cartConfirmation)
             .setHeader(KafkaHeaders.TOPIC, "cart-topic")
             .build();
        kafkaTemplate.send(message);
    }

}
