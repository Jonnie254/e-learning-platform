package com.jonnie.elearning.services;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    // method to register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(
            @RequestBody @Valid UserRegistrationRequest userRegistrationRequest
    ) throws MessagingException {
        userService.registerUser(userRegistrationRequest);
        return ResponseEntity.accepted().build();
    }

    // method to activate an account
    @GetMapping("/activate-account")
    public void activateAccount(    
            @RequestParam String token
    ) throws MessagingException {
        userService.activateAccount(token);
    }

    // method to authenticate the user
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateUser(
            @RequestBody @Valid UserAuthenticationRequest userAuthenticationRequest
    ) {
        return ResponseEntity.ok(userService.authenticateUser(userAuthenticationRequest));
    }

    // method to update the user's details
    @PutMapping("/update-user")
    public ResponseEntity<Void> updateUser(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestBody @Valid UserUpdateRequest userUpdateRequest
    ) {
        // Enforce role-based authorization
        if (!"STUDENT".equalsIgnoreCase(userRole)) {
            log.warn("Unauthorized role: {}. Only ADMIN role can update user details.", userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        // Proceed with the update
        userService.updateUser(userId, userRole, userUpdateRequest);
        return ResponseEntity.ok().build();
    }
    // method to request to become an instructor
    @PostMapping("/request-instructor")
    public ResponseEntity<Void> requestInstructor(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid UserRoleRequest userRoleRequest
    ) {
        userService.requestInstructor(userId, userRoleRequest);
        return ResponseEntity.accepted().build();
    }

    // method to make a user in active
    @PutMapping("/deactivate-user")
    public ResponseEntity<Void> deactivateUser(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        log.info("Received request with X-User-Id: {}, X-User-Role: {}", userId, userRole);
        // Enforce role-based authorization
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            log.warn("Unauthorized role: {}. Only ADMIN role can deactivate user.", userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Proceed with the deactivation
        userService.deactivateUser(userId);
        return ResponseEntity.ok().build();
    }




}
