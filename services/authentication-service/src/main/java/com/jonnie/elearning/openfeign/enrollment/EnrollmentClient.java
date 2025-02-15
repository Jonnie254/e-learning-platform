package com.jonnie.elearning.openfeign.enrollment;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "enrollment-service",
        url = "${application.config.enrollment-url}"
)
public interface EnrollmentClient {
   @PostMapping("/check-user-enrollment-status/{userId}")
    boolean hasEnrollments(@PathVariable String userId);
}
