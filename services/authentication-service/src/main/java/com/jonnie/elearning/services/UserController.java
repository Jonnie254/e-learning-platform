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
    public ResponseEntity<?> authenticateUser(
            @RequestBody @Valid UserAuthenticationRequest userAuthenticationRequest
    ) {
        return ResponseEntity.ok(userService.authenticateUser(userAuthenticationRequest));
    }

}
