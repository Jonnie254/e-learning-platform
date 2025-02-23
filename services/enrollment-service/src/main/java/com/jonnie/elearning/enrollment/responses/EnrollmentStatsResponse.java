package com.jonnie.elearning.enrollment.responses;


import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentStatsResponse {
    private int totalEnrollments;
}
