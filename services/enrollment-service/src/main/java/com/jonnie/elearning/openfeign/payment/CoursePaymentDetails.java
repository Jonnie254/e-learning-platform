package com.jonnie.elearning.openfeign.payment;

import java.math.BigDecimal;

public record CoursePaymentDetails(
        String courseId,
        String instructorId,
        BigDecimal price
) {
}
