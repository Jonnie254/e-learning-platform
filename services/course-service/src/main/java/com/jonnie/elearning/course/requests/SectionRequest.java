package com.jonnie.elearning.course.requests;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record SectionRequest(
        String sectionId,
        @NotEmpty(message = "Section name is required")
        @NotNull(message = "Section name is required")
        String sectionName,
        @NotEmpty(message = "Section description is required")
        @NotNull(message = "Section description is required")
        String sectionDescription
) {

}
