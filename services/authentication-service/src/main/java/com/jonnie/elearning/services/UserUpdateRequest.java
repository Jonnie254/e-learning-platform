package com.jonnie.elearning.services;

public record UserUpdateRequest(
        String id,
        String firstName,
        String lastName,
        String email,
        String password,
        String role
) {
}
