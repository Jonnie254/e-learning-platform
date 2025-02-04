package com.jonnie.elearning;

import com.jonnie.elearning.cart.CartResponse;
import com.jonnie.elearning.cart.CartService;
import com.jonnie.elearning.cartitem.CartItemResponse;
import com.jonnie.elearning.cartitem.CartItemService;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.progress.ProgressService;
import com.jonnie.elearning.utils.ROLE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final CartItemService cartItemService;
    private final CartService cartService;
    private final ProgressService progressService;


    private void validateUser(String userId, String userRole, ROLE requiredRole) {
        if (userId == null || userId.isEmpty() || userRole == null || userRole.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "User ID and role are required");
        }
        if (!requiredRole.name().equalsIgnoreCase(userRole)) {
            throw new ResponseStatusException(FORBIDDEN, "You are not allowed to perform this operation");
        }
    }

    @PostMapping("/add-course-cart/{course-id}")
    public ResponseEntity<String> addCartItems(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("course-id") String courseId
    ) {
        log.info("Adding course to cart: {}", courseId);
        log.info("User ID: {}", userId);
        log.info("User Role: {}", userRole);

        validateUser(userId, userRole, ROLE.STUDENT);

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

    //Removed unnecessary validationResponse check
    @GetMapping("/get-all-items")
    public ResponseEntity<PageResponse<CartItemResponse>> getAllCartItems(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        return ResponseEntity.ok(cartItemService.getAllCartItems(userId, page, size));
    }

    // Corrected the validation call
    @DeleteMapping("/remove-cart-item/{course-id}")
    public ResponseEntity<String> removeCartItem(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("course-id") String courseId
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
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

    //method to get the cart
    @GetMapping("/get-cart")
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    //method to check out from the cart
    @PostMapping("/checkout-cart")
    public ResponseEntity<String> checkoutCart(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        try {
            String approvalUrl = cartService.checkoutCart(userId);
            return ResponseEntity.ok(approvalUrl);
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }

    @PostMapping("/initialize-progress/{course-id}")
    public ResponseEntity<String> initializeProgress(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("course-id") String courseId
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        try {
            progressService.initializeUserProgress(userId, courseId);
            return ResponseEntity.ok("Progress initialized successfully");
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }
}
