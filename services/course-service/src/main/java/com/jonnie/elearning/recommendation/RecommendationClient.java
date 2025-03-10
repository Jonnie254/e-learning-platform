package com.jonnie.elearning.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
        name = "recommendationClient",
        url = "http://localhost:5000")
public interface RecommendationClient {
    @GetMapping("/recommend")
    Map<String, Object> getRecommendations(@RequestParam("user_id") String userId);

}
