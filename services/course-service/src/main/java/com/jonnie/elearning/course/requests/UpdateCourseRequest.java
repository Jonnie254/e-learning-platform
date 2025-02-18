package com.jonnie.elearning.course.requests;

import java.util.List;

public record UpdateCourseRequest(
        String courseId,
        String courseName,
        String courseDescription,
        String categoryId,
        String price,
        List<String> tagIds,
        List<String> whatYouWillLearn
) {
}
