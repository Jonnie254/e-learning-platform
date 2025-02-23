package com.jonnie.elearning.payment.responses;


import com.jonnie.elearning.openfeign.courses.CourseDetailsResponse;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseEarningResponse {
    private CourseDetailsResponse courseDetailsResponse;
    private BigDecimal totalEarning;
}
