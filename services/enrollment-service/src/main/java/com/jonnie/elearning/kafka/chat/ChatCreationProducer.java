package com.jonnie.elearning.kafka.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCreationProducer {
    private final KafkaTemplate<String, ChatCreationRequest> kafkaTemplate;

    public void sendChatCreation(ChatCreationRequest chatCreationRequest) {
        log.info("Sending chat creation: {}", chatCreationRequest);
        Message<ChatCreationRequest> message = MessageBuilder
                .withPayload(chatCreationRequest)
                .setHeader(KafkaHeaders.TOPIC, "chat-topic")
                .build();
        kafkaTemplate.send(message);
    }
}
