package com.jonnie.elearning;

import com.jonnie.elearning.cartitem.CartItemResponse;
import com.jonnie.elearning.cartitem.CartItemService;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.utils.ROLE;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final CartItemService cartItemService;

    private ResponseEntity<String> validateUser(String userId, String userRole, ROLE requiredRole) {
        if (userId == null || userId.isEmpty() || userRole == null || userRole.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST).body("User ID and role are required");
        }
        if (!requiredRole.name().equals(userRole)) {
            return ResponseEntity.status(FORBIDDEN).body("You are not allowed to perform this operation");
        }
        return null; // Null means validation passed
    }


    @PostMapping("/add-course-cart/{course-id}")
    public ResponseEntity<String> addCartItems(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("course-id") String courseId
    ) {
        ResponseEntity<String> validationResponse = validateUser(userId, userRole, ROLE.STUDENT);
        if (validationResponse != null) {
            return validationResponse;
        }
        try {
            cartItemService.addCartItem(userId, courseId);
            return ResponseEntity.ok("Course added to cart successfully");
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }

    @GetMapping("/get-all-items")
    public ResponseEntity<PageResponse<CartItemResponse>> getAllCartItems(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        ResponseEntity<String> validationResponse = validateUser(userId, userRole, ROLE.STUDENT);
        if (validationResponse != null) {
            return ResponseEntity.status(validationResponse.getStatusCode()).body(null);
        }
        return ResponseEntity.ok(cartItemService.getAllCartItems(userId, page, size));
    }

    @DeleteMapping("/remove-cart-item/{course-id}")
    public ResponseEntity<String> removeCartItem(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("course-id") String courseId
    ) {
        ResponseEntity<String> validationResponse = validateUser(userId, userRole, ROLE.STUDENT);
        if (validationResponse != null) {
            return validationResponse;
        }
        try {
            cartItemService.removeCartItem(userId, courseId);
            return ResponseEntity.ok("Course removed from cart successfully");
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }
}
