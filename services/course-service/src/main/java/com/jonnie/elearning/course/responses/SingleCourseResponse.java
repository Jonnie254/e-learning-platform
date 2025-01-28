package com.jonnie.elearning.course.responses;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleCourseResponse {
    private String courseId;
    private String courseName;
    private String courseImageUrl;
    private String instructorName;
    private BigDecimal price;
    private String description;
    private List<String> whatYouWillLearn;
}
