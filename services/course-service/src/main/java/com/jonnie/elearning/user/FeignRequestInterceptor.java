package com.jonnie.elearning.user;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Slf4j
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Extract the token from the incoming HTTP request
        String token = getCurrentRequestToken();
        if (token != null) {
            // Remove "Bearer " prefix if present
            token = token.replace("Bearer ", "").trim();
            log.info("Extracted Token: {}", token);

            // Add the token to the Authorization header of the outgoing request
            template.header("Authorization", "Bearer " + token);
        }
    }

    private String getCurrentRequestToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("No current HTTP request found");
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader("Authorization");
    }
}
