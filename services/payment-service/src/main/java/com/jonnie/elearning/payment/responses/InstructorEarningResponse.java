package com.jonnie.elearning.payment.responses;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstructorEarningResponse {
    BigDecimal totalEarning;
    List<CourseEarningResponse> courseEarningResponse;
}
