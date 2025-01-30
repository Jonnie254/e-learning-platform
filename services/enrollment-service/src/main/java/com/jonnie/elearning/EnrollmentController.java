package com.jonnie.elearning;

import com.jonnie.elearning.cart.CartResponse;
import com.jonnie.elearning.cart.CartService;
import com.jonnie.elearning.cartitem.CartItemResponse;
import com.jonnie.elearning.cartitem.CartItemService;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.enrollment.EnrollmentRequest;
import com.jonnie.elearning.enrollment.EnrollmentService;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.utils.ROLE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final CartItemService cartItemService;
    private final CartService cartService;
    private final EnrollmentService enrollmentService;

    private ResponseEntity<String> validateUser(String userId, String userRole, ROLE requiredRole) {
        if (userId == null || userId.isEmpty() || userRole == null || userRole.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST).body("User ID and role are required");
        }
        if (!requiredRole.name().equals(userRole)) {
            return ResponseEntity.status(FORBIDDEN).body("You are not allowed to perform this operation");
        }
        return null;
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

    // retrieve all items in the cart
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

    // remove course from cart
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

    //get cart belonging to a user
    @GetMapping("/get-cart")
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        ResponseEntity<String> validationResponse = validateUser(userId, userRole, ROLE.STUDENT);
        if (validationResponse != null) {
            return ResponseEntity.status(validationResponse.getStatusCode()).body(null);
        }
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/checkout-cart")
    public ResponseEntity<String> checkoutCart(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        ResponseEntity<String> validationResponse = validateUser(userId, userRole, ROLE.STUDENT);
        if (validationResponse != null) {
            return validationResponse;
        }

        try {
            // Call checkoutCart() and capture the approval URL
            String approvalUrl = cartService.checkoutCart(userId);

            return ResponseEntity.ok(approvalUrl); // ✅ Return the PayPal approval link
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }

    // finalize enrollment
    @PostMapping("/finalize-enrollment")
    public ResponseEntity<String> finalizeEnrollment(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestBody EnrollmentRequest enrollmentRequest
            ) {
        ResponseEntity<String> validationResponse = validateUser(userId, userRole, ROLE.STUDENT);
        if (validationResponse != null) {
            return validationResponse;
        }
        try {
            enrollmentService.finalizeEnrollment(enrollmentRequest);
            return ResponseEntity.ok("Enrollment finalized successfully");
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }
}
