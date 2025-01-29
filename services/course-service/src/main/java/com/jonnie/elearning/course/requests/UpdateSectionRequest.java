package com.jonnie.elearning.course.requests;




import lombok.Builder;


@Builder
public record UpdateSectionRequest(
        String sectionName,
        String sectionDescription
) {
}
