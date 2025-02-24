package com.jonnie.elearning.course.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record CourseRequest(
        String courseId,
        @NotNull(message = "Course name is required")
        @NotEmpty(message = "Course name is required")
        String courseName,
        @NotNull(message = "course description is required")
        @NotNull(message = "Course description is required")
        @NotEmpty(message = "Course description is required")
        String courseDescription,
        @NotNull(message = "Category id is required")
        @NotEmpty(message = "Category id is required")
        String categoryId,
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,
        @NotNull(message = "Tag ids are required")
        @NotEmpty(message = "Tag ids are required")
        List<String> tagIds,
        @NotNull(message = "What you will learn is required")
        @NotEmpty(message = "What you will learn is required")
        List<String> whatYouWillLearn
) {
}
