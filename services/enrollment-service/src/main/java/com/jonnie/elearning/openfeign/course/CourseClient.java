package com.jonnie.elearning.openfeign.course;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@FeignClient(
        name = "course-service",
        url = "${application.config.course-url}"
)
public interface CourseClient {
    @GetMapping("/cart-item/{course-id}")
    Optional<CourseResponse> getCourseById(
            @PathVariable("course-id") String courseId
    );
    @GetMapping("/sections/{course-id}")
    CourseSectionsResponse getCourseSectionIds(@PathVariable("course-id") String parameter);
}
