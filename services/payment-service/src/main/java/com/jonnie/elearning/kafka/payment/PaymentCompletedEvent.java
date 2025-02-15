package com.jonnie.elearning.kafka.payment;


import com.jonnie.elearning.payment.PaymentRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;


@Slf4j
public class PaymentCompletedEvent extends ApplicationEvent {
    private final PaymentRecord paymentRecord;

    public PaymentCompletedEvent(Object source, PaymentRecord paymentRecord) {
        super(source);
        this.paymentRecord = paymentRecord;
    }

    public PaymentRecord getPaymentRecord() {
        return paymentRecord;
    }
}