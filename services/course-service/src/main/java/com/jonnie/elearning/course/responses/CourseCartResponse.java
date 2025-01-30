package com.jonnie.elearning.course.responses;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseCartResponse {
    private String courseId;
    private String courseName;
    private BigDecimal price;
    private String instructorId;
    private String instructorName;
    private String courseImageUrl;
}
