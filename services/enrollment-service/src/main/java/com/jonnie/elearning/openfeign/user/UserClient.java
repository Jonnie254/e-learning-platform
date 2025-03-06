package com.jonnie.elearning.openfeign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;
import java.util.Optional;

@FeignClient(
        name = "authentication-service",
        url = "${application.config.authentication-url}"
)
public interface UserClient {
    @GetMapping("/user-details")
    Optional<UserResponse> getUser();

    @GetMapping("/has-requested-role/{userId}")
    Optional<Boolean> hasRequestedToBeInstructor(@PathVariable("userId") String userId);
}
