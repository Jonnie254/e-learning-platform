package com.jonnie.elearning.user;

public record UserUpdateRequest(
        String id,
        String firstName,
        String lastName,
        String email,
        String password,
        String role
) {
}
