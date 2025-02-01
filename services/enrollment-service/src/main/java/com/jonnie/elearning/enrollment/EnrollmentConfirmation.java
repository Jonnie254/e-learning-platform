package com.jonnie.elearning.enrollment;

import com.jonnie.elearning.utils.PaymentMethod;

import java.util.List;

public record EnrollmentConfirmation(
        String enrollmentId,
        List<String> courseIds,
        String userId,
        boolean isPaid,
        PaymentMethod paymentMethod,
        List<String> instructorIds
) {

}
