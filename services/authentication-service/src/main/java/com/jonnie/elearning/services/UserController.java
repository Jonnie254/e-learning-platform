package com.jonnie.elearning.services;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
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
            @RequestHeader("X-User-Id") String userId, // Passed by the gateway
            @RequestHeader("X-User-Role") String userRole, // Optionally, for role-based authorization
            @RequestBody @Valid UserUpdateRequest userUpdateRequest
    ) {
        userService.updateUser(userId, userRole, userUpdateRequest);
        return ResponseEntity.accepted().build();
    }



}
