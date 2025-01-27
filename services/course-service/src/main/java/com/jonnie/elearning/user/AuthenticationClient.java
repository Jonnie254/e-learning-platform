package com.jonnie.elearning.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "authentication-service", url = "${application.config.authentication-url}")
public interface AuthenticationClient {

    @GetMapping("/user/{user-id}")
    Optional<UserResponse> findUserById(@PathVariable("user-id") String userId);
}