package com.jonnie.elearning.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "course-service",
        url = "${application.config.course-url}"
)
public interface CourseClient {
    @PostMapping("/instructor/{courseId}")
    String getInstructorId(@PathVariable("courseId") String courseId);
}
