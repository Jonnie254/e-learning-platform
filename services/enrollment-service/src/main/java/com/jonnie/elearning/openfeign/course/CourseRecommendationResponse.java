package com.jonnie.elearning.openfeign.course;


import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRecommendationResponse {
    private String courseId;
    private String courseName;
    private BigDecimal price;
    private String instructorId;
    private String instructorName;
    private String courseImageUrl;
}

