package com.jonnie.elearning.openfeign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@FeignClient(
        name = "authentication-service",
        url = "${application.config.authentication-url}"
)
public interface UserClient {
    @GetMapping("/user-details")
    Optional<UserResponse> getUser();
}
