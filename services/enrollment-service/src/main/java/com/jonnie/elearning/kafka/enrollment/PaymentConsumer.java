package com.jonnie.elearning.kafka.enrollment;

import com.jonnie.elearning.enrollment.Enrollment;
import com.jonnie.elearning.repositories.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {
    private final EnrollmentRepository enrollmentRepository;

    //listening to the enrollment topic from the payment service
    @KafkaListener(topics = "enrollment-topic")
    public void consumeEnrollmentSucess(EnrollmentConfirmation enrollmentConfirmation){
        log.info("Consumed enrollment confirmation: {}", enrollmentConfirmation);
        enrollmentRepository.save(
                Enrollment.builder()
                        .courseIds(enrollmentConfirmation.courseIds())
                        .userId(enrollmentConfirmation.userId())
                        .isPaid(enrollmentConfirmation.isPaid())
                        .paymentMethod(enrollmentConfirmation.paymentMethod())
                        .instructorIds(enrollmentConfirmation.instructorIds())
                        .build()
        );
    }

}
