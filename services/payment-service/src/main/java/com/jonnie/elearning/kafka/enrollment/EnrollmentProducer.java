package com.jonnie.elearning.kafka.enrollment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EnrollmentProducer {
    private final KafkaTemplate<String, EnrollmentConfirmation> kafkaTemplate;
    public void sendEnrollmentConfirmation(EnrollmentConfirmation enrollmentConfirmation){
        log.info("Sending enrollment confirmation: {}", enrollmentConfirmation);
        Message<EnrollmentConfirmation>  message = MessageBuilder
                .withPayload(enrollmentConfirmation)
                .setHeader(KafkaHeaders.TOPIC, "enrollment-topic")
                .build();
        kafkaTemplate.send(message);
    }
}
