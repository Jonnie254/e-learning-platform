package com.jonnie.elearning.openfeign.course;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "course-service",
        url = "${application.config.course-url}"
)
public interface CourseClient {
    @PostMapping("/instructor/{courseId}")
    String getInstructorId(@PathVariable("courseId") String courseId);

    @PostMapping("/course-chat-info")
    List<CourseChatResponse> getCoursesInfo(@RequestBody List<String> courseIds);

}
