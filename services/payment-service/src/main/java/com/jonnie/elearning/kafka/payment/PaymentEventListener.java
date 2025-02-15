package com.jonnie.elearning.kafka.payment;

import com.jonnie.elearning.kafka.cart.CartNotification;
import com.jonnie.elearning.kafka.cart.CartProducer;
import com.jonnie.elearning.kafka.enrollment.EnrollmentConfirmation;
import com.jonnie.elearning.kafka.enrollment.EnrollmentProducer;
import com.jonnie.elearning.kafka.notification.NotificationProducer;
import com.jonnie.elearning.kafka.notification.PaymentNotificationRequest;
import com.jonnie.elearning.payment.PaymentRecord;
import com.jonnie.elearning.utils.CartStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final NotificationProducer notificationProducer;
    private final CartProducer cartProducer;
    private final EnrollmentProducer enrollmentProducer;


    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent paymentCompletedEvent){
        PaymentRecord paymentRecord = paymentCompletedEvent.getPaymentRecord();
        log.info("Handling payment completed event: {}", paymentRecord);

        PaymentNotificationRequest paymentNotificationRequest = new PaymentNotificationRequest(
                paymentRecord.getCartReference(),
                paymentRecord.getAmount(),
                paymentRecord.getPaymentMethod(),
                paymentRecord.getCustomerFirstName(),
                paymentRecord.getCustomerLastName(),
                paymentRecord.getCustomerEmail()
        );
        CartNotification cartConfirmation = new CartNotification(
                paymentRecord.getCartReference(),
                CartStatus.CHECKED_OUT
        );
        EnrollmentConfirmation enrollmentConfirmation = new EnrollmentConfirmation(
                paymentRecord.getCourseIds(),
                paymentRecord.getUserId(),
                paymentRecord.isPaid(),
                paymentRecord.getPaymentMethod(),
                paymentRecord.getInstructorIds()
        );

        //send kafka messages
        notificationProducer.sendPaymentNotification(paymentNotificationRequest);
        cartProducer.cartConfirmationStatus(cartConfirmation);
        enrollmentProducer.sendEnrollmentConfirmation(enrollmentConfirmation);

    }
}
