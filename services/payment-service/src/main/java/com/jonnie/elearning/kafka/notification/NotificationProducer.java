package com.jonnie.elearning.kafka.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {
   private final KafkaTemplate<String, PaymentNotificationRequest> kafkaTemplate;
   public void sendPaymentNotification(PaymentNotificationRequest paymentNotificationRequest) {
      log.info("Sending payment notification: <{}>", paymentNotificationRequest);
      kafkaTemplate.send("payment-topic", paymentNotificationRequest);
   }

}
