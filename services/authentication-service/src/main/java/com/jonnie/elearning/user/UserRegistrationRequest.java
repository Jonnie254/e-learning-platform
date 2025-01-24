package com.jonnie.elearning.user;

import com.jonnie.elearning.role.ROLE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UserRegistrationRequest(
        String id,
        @NotNull(message = "First name is required")
        String firstName,
        @NotNull(message = "Last name is required")
        String lastName,
        @NotNull(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,
        @NotNull(message = "Email is required")
        @Email(message = "Email is invalid, enter a valid email")
        String email,
        @NotNull(message = "Role is required")
        ROLE role
) {
}
