package com.jonnie.elearning.openfeign.course;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseRated {
    private String courseId;
    private String courseName;
    private BigDecimal price;
    private String instructorId;
    private String instructorName;
    private String courseImageUrl;
    private double rating;
}
