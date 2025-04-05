package com.jonnie.elearning.course.responses;


import com.jonnie.elearning.category.CategoryResponse;
import com.jonnie.elearning.tag.TagResponse;
import com.jonnie.elearning.utils.SkillLevel;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstructorFullCourseDetailsResponse {
    private String courseId;
    private String courseName;
    private String courseDescription;
    private String courseUrlImage;
    private CategoryResponse category;
    private List<TagResponse> tags;
    private List<String> whatYouWillLearn;
    private BigDecimal price;
    private SkillLevel skillLevel;
}
