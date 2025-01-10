package com.jonnie.elearning.services;


import jakarta.validation.constraints.*;


public record UserAuthenticationRequest(
        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        @NotEmpty(message = "Email is required")
        String email,
        @NotBlank(message = "Password is required")
        @NotEmpty(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password

) {
}
