package com.jonnie.elearning.enrollment;

import org.springframework.stereotype.Service;

@Service
public class EnrollmentMapper {
    public Enrollment toEnrollment(EnrollmentConfirmation enrollmentRequest) {
        return Enrollment.builder()
                .courseIds(enrollmentRequest.courseIds())
                .userId(enrollmentRequest.userId())
                .isPaid(enrollmentRequest.isPaid())
                .paymentMethod(enrollmentRequest.paymentMethod())
                .instructorIds(enrollmentRequest.instructorIds())
                .build();
    }
}
