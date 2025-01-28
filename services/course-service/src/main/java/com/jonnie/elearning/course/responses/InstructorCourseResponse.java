package com.jonnie.elearning.course.responses;


import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstructorCourseResponse {
    private String courseId;
    private String courseName;
    private BigDecimal price;
    private String category;
    private String courseImageUrl;
}
