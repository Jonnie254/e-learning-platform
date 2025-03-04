package com.jonnie.elearning.openfeign.user;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "authentication-service",
        url = "${application.config.authentication-url}"
)
public interface AuthenticationClient {
    @PostMapping("/username/{userId}")
    String getUserSenderNames(@PathVariable("userId") String userId);
}
