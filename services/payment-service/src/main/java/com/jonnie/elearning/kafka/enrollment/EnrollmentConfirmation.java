package com.jonnie.elearning.kafka.enrollment;

import com.jonnie.elearning.utils.PaymentMethod;

import java.util.List;

public record EnrollmentConfirmation(
        List<String> courseIds,
        String userId,
        boolean isPaid,
        PaymentMethod paymentMethod,
        List<String> instructorIds
) {

}
