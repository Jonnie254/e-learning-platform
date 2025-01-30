package com.jonnie.elearning.enrollment;

import com.jonnie.elearning.utils.PaymentMethod;

public record EnrollmentRequest(
        String enrollmentId,
        String courseId,
        String userId,
        boolean isPaid,
        PaymentMethod paymentMethod,
        String instructorId
) {

}
