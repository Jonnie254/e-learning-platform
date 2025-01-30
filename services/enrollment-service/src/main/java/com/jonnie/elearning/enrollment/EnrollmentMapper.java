package com.jonnie.elearning.enrollment;

import com.jonnie.elearning.utils.PaymentMethod;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentMapper {
    public Enrollment toEnrollment(EnrollmentRequest enrollmentRequest) {
        return Enrollment.builder()
                .courseId(enrollmentRequest.courseId())
                .userId(enrollmentRequest.userId())
                .isPaid(true)
                .paymentMethod(enrollmentRequest.paymentMethod())
                .instructorId(enrollmentRequest.instructorId())
                .build();
    }
}
