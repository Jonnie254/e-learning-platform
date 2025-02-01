package com.jonnie.elearning.notification;

import com.jonnie.elearning.kafka.Payment.PaymentConfirmation;
import com.jonnie.elearning.kafka.cart.CartConfirmation;
import com.jonnie.elearning.utils.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class Notification {
    @Id
    private String notificationId;
    private NotificationType type;
    private LocalDateTime notificationDate;
    private CartConfirmation cartConfirmation;
    private PaymentConfirmation paymentConfirmation;
}
