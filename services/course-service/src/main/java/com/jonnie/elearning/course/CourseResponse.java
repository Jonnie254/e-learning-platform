package com.jonnie.elearning.course;

import com.jonnie.elearning.category.Category;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseResponse {
    private String courseId;
    private String courseName;
    private String courseUrlImage;
    private String InstructorName;
    private BigDecimal price;
    private String description;
    private String category;
    private List<String> whatYouWillLearn;
    private Content content;

}
