package com.jonnie.elearning.kafka.Consumer;


import com.jonnie.elearning.email.EmailService;
import com.jonnie.elearning.kafka.Payment.PaymentConfirmation;
import com.jonnie.elearning.kafka.cart.CartConfirmation;
import com.jonnie.elearning.notification.Notification;
import com.jonnie.elearning.notification.NotificationRepository;
import com.jonnie.elearning.utils.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "cart-topic")
    public void consumerCartNotification(CartConfirmation cartConfirmation){
        log.info("Consuming notification from cart topic: <{}>", cartConfirmation);
        notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.CART_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .cartConfirmation(cartConfirmation)
                        .build()
        );
        var customerName = cartConfirmation.userResponse().getFirstName() + " " + cartConfirmation.userResponse().getLastName();
        emailService.sendCartConfirmationEmail(
                cartConfirmation.userResponse().getEmail(),
                customerName,
                cartConfirmation.totalAmount(),
                cartConfirmation.cartReference(),
                cartConfirmation.cartItems()
        );
    }

    @KafkaListener(topics = "payment-topic")
    public void consumerPaymentNotification(PaymentConfirmation paymentConfirmation) {
        log.info("Consuming notification from payment topic: <{}>", paymentConfirmation);
        notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );
        var customerName = paymentConfirmation.customerFirstName() + " " + paymentConfirmation.customerLastName();
        emailService.sendPaymentConfirmationEmail(
                paymentConfirmation.customerEmail(),
                customerName,
                paymentConfirmation.amount(),
                paymentConfirmation.cartReference()
        );
    }
}
