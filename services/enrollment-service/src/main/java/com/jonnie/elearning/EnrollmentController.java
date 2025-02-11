package com.jonnie.elearning;

import com.jonnie.elearning.cart.CartResponse;
import com.jonnie.elearning.cart.CartService;
import com.jonnie.elearning.cartitem.CartItemResponse;
import com.jonnie.elearning.cartitem.CartItemService;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.enrollment.EnrollmentResponse;
import com.jonnie.elearning.enrollment.EnrollmentService;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.progress.ProgressService;
import com.jonnie.elearning.progress.SectionStatusResponse;
import com.jonnie.elearning.utils.ROLE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final CartItemService cartItemService;
    private final CartService cartService;
    private final ProgressService progressService;
    private final EnrollmentService enrollmentService;


    private void validateUser(String userId, String userRole, ROLE requiredRole) {
        if (userId == null || userId.isEmpty() || userRole == null || userRole.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "User ID and role are required");
        }
        if (!requiredRole.name().equalsIgnoreCase(userRole)) {
            throw new ResponseStatusException(FORBIDDEN, "You are not allowed to perform this operation");
        }
    }

    @PostMapping("/add-course-cart/{course-id}")
    public ResponseEntity<Map<String, String>> addCartItems(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("course-id") String courseId
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        try {
            cartItemService.addCartItem(userId, courseId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Course added to cart successfully");
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred. Please try again later."));
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
    public ResponseEntity<Map<String, String>> removeCartItem(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("course-id") String courseId
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        try {
            cartItemService.removeCartItem(userId, courseId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Course added to cart successfully");
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred. Please try again later."));
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
    public ResponseEntity<Map<String, String>> checkoutCart(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        try {
            String approvalUrl = cartService.checkoutCart(userId);
            Map<String, String> response = new HashMap<>();
            response.put("approvalUrl", approvalUrl);
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred. Please try again later."));
        }
    }

    //method to get the courses user has enrolled in
    @PostMapping("/get-enrolled-courses-ids")
    public ResponseEntity<List<String>> getEnrolledCourses(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole
    ){
        validateUser(userId, userRole, ROLE.STUDENT);
        return ResponseEntity.ok(enrollmentService.getEnrolledCourses(userId));
    }

    //method to get the details of the enrolled in
    @GetMapping("/enrolled-courses-details")
    public ResponseEntity<PageResponse<EnrollmentResponse>> getEnrolledCoursesDetails(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        validateUser(userId, userRole, ROLE.STUDENT);
        return ResponseEntity.ok(enrollmentService.getEnrolledCoursesDetails(userId, page, size));
    }

    //method to initialize progress
    @PostMapping("/initialize-progress/{courseId}")
    public ResponseEntity<Map<String, String>> initializeProgress(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("courseId") String courseId
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        try {
            progressService.initializeUserProgress(userId, courseId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Progress initialized successfully");
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred. Please try again later."));
        }
    }

    //get the status of the section
    @GetMapping("/get-section-status/{sectionId}")
    public ResponseEntity<SectionStatusResponse> getSectionStatus(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("sectionId") String sectionId
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        return ResponseEntity.ok(progressService.getSectionStatus(userId, sectionId));
    }

    //method to toggle the section status
    @PutMapping("/toggle-section-status/{sectionId}")
    public ResponseEntity<Map<String, String>> toggleSectionStatus(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable("sectionId") String sectionId
    ) {
        validateUser(userId, userRole, ROLE.STUDENT);
        try {
            progressService.toggleSectionStatus(userId, sectionId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Section status toggled successfully");
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred. Please try again later."));
        }
    }

}
