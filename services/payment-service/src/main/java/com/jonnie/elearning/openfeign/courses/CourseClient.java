package com.jonnie.elearning.openfeign.courses;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(
        name = "course-service",
        url = "${application.config.course-url}"
)
public interface CourseClient {
    @PostMapping("/courses-earnings")
    List<CourseDetailsResponse> getCoursesByIds(List<String> courseIds);

    @PostMapping("/course-details-revenue")
    CourseDetailsResponse getCourseDetails(String key);
}
