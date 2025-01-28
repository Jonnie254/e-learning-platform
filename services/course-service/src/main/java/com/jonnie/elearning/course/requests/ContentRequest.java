package com.jonnie.elearning.course.requests;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;

import java.util.List;

@Builder
public record ContentRequest(
        String contentId, // Null for new content
        @NotNull(message = "Content name is required")
        @NotEmpty(message = "Content name is required")
        String name,
        @NotNull(message = "Course ID is required")
        String courseId,
        List<SectionRequest> sections
) {
}
