package com.jonnie.elearning.openfeign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;
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

    @PostMapping("/users-profile-details")
    List<UserProfileResponse> getUserProfileDetails(@RequestBody List<String> userIds);
}
