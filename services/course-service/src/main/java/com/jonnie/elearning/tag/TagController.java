package com.jonnie.elearning.tag;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping("/create-tag")
    public ResponseEntity<String> create(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestBody TagRequest tagRequest
    ){
        if(!userRole.equals("ADMIN")){
            return ResponseEntity.badRequest().body("You are not authorized to create a tag");
        }
        return ResponseEntity.ok(tagService.createTag(tagRequest, userId, userRole));
    }
}
