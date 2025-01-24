package com.jonnie.elearning.services;

import com.jonnie.elearning.role.ROLE;
import com.jonnie.elearning.user.User;
import com.jonnie.elearning.user.UserRegistrationRequest;
import com.jonnie.elearning.user.UserResponse;
import com.jonnie.elearning.user.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ToString
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    public User toUser(UserRegistrationRequest userRegistrationRequest) {
        return User.builder()
                .id(userRegistrationRequest.id())
                .firstName(userRegistrationRequest.firstName())
                .lastName(userRegistrationRequest.lastName())
                .password(passwordEncoder.encode(userRegistrationRequest.password()))
                .email(userRegistrationRequest.email())
                .role(userRegistrationRequest.role())
                .build();
    }

    public User toUpdate(User existingUser, UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest.firstName() != null) {
            existingUser.setFirstName(userUpdateRequest.firstName());
        }
        if (userUpdateRequest.lastName() != null) {
            existingUser.setLastName(userUpdateRequest.lastName());
        }
        if (userUpdateRequest.email() != null) {
            existingUser.setEmail(userUpdateRequest.email());
        }
        if (userUpdateRequest.password() != null && !userUpdateRequest.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.password()));
        }

        if (userUpdateRequest.role() != null) {
            existingUser.setRole(ROLE.valueOf(userUpdateRequest.role()));
        }
        return existingUser;
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .profilePicUrl(user.getProfilePicUrl())
                .build();
        return response;
    }

}
