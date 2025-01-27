package com.jonnie.elearning.category;

import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.utils.ROLE;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create-category")
    public ResponseEntity<String> createCategory(
            @RequestHeader("X-User-Role") String userRole,
            @RequestBody @Valid CategoryRequest categoryRequest
            ){
        if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(categoryService.createCategory(categoryRequest));
    }

    @GetMapping("/all-categories")
    public ResponseEntity<PageResponse<CategoryResponse>> findAll(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(name="page", defaultValue = "0", required = false) int page,
            @RequestParam(name="size", defaultValue = "10", required = false) int size
    ){
        if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole) && !ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(categoryService.findAll(page, size));
    }

    @GetMapping("/category/{category-id}")
    public ResponseEntity<CategoryResponse> findById(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("category-id") String categoryId
    ){
        if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(categoryService.findById(categoryId));
    }
}
