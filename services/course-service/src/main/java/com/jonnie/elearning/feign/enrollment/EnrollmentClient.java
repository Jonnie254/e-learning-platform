package com.jonnie.elearning.feign.enrollment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(
        name = "enrollment-service",
        url = "${application.config.enrollment-url}"
)
public interface EnrollmentClient {
    @PostMapping("/get-enrolled-courses-ids")
     List<String> getEnrolledCourses(String userId);

    @PostMapping("/initialize-progress/{courseId}")
    void initializeProgress(@PathVariable("courseId") String courseId);
}
