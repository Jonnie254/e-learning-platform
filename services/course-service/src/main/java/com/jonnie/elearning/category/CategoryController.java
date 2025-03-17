package com.jonnie.elearning.category;

import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.BusinessException;
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
public class CategoryController {
    private final CategoryService categoryService;

    //method to create a new category
    @PostMapping("/create-category")
    public ResponseEntity<Map<String, String>> createCategory(
            @RequestHeader("X-User-Role") String userRole,
            @RequestBody @Valid CategoryRequest categoryRequest) {

        if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to perform this action"));
        }
        try {
            String categoryId = categoryService.createCategory(categoryRequest);
            return ResponseEntity.ok(Map.of("categoryId", categoryId));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    //method to get all categories
    @GetMapping("/all-categories")
    public ResponseEntity<PageResponse<CategoryResponse>> findAll(
            @RequestParam(name="page", defaultValue = "0", required = false) int page,
            @RequestParam(name="size", defaultValue = "10", required = false) int size
    ){
        return ResponseEntity.ok(categoryService.findAll(page, size));
    }

    //method to get a category by id
    @GetMapping("/category/{category-id}")
    public ResponseEntity<CategoryResponse> findById(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("category-id") String categoryId
    ) {
        if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(categoryService.findById(categoryId));
    }

    //method to update a category
    @PutMapping("/update-category/{category-id}")
    public ResponseEntity<Map<String, String>> update(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("category-id") String categoryId,
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to perform this action"));
        }
        try {
            categoryService.updateCategory(categoryId, categoryRequest);
            return ResponseEntity.ok(Map.of("message", "Category updated successfully"));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
}
