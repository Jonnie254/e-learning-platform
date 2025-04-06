package com.jonnie.elearning.recommendation;

import com.jonnie.elearning.recommendation.requests.KnowYouRequest;
import com.jonnie.elearning.recommendation.responses.KnowYouResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class KnowYouController {
    private final KnowYouService knowYouService;

    // method to create a know you
    @PostMapping("/create-know-you")
    public ResponseEntity<Map<String, String>> createKnowYou(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid KnowYouRequest knowYouRequest
    ){
        knowYouService.createKnowYou(userId, knowYouRequest);
        return ResponseEntity.ok().body(Map.of("message",
                "successfully created know you "));
    }

    // method to check if a user has a know you
    @GetMapping("/check-user-know-you")
    public ResponseEntity<Boolean> checkUserKnowYou(
            @RequestHeader("X-User-Id") String userId
    ){
        boolean hasKnowYou = knowYouService.checkUserKnowYou(userId);
        return ResponseEntity.ok(hasKnowYou);
    }

    // method to get a know you
    @GetMapping("/get-user-know-you")
    public ResponseEntity<KnowYouResponse> getUserKnowYou(
            @RequestHeader ("X-User-Id") String userId
    ){
        KnowYouResponse knowYouResponse = knowYouService.getUserKnowYou(userId);
        return ResponseEntity.ok(knowYouResponse);
    }
}
