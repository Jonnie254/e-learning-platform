package com.jonnie.elearning.tag;


import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.utils.ROLE;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping("/create-tag")
    public ResponseEntity<Map<String, String>> create(
            @RequestHeader("X-User-Role") String userRole,
            @RequestBody @Valid TagRequest tagRequest
    ){
        if(!userRole.equalsIgnoreCase(ROLE.ADMIN.name())){
            Map<String, String> response = new  HashMap<> ();
            response.put("message", "You are not authorized to perform this operation");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        String tagId = tagService.createTag(tagRequest);
        Map<String, String> response = new HashMap<>();
        response.put("tagId", tagId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/tags")
    public ResponseEntity<PageResponse<TagResponse>> findAll(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(name="page", defaultValue = "0", required = false) int page,
            @RequestParam(name="size", defaultValue = "10", required = false) int size
    ){
        if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole) && !ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(tagService.findAll(page, size));
    }

    @GetMapping("/tag/{tag-id}")
    public ResponseEntity<TagResponse> findById(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("tag-id") String tagId
    ){
        if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole) && !ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(tagService.findById(tagId));
    }
}
