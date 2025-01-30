package com.jonnie.elearning.openfeign.user;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "authentication-service",
        url = "${application.config.authentication-url}"
)
public interface UserClient {
}
