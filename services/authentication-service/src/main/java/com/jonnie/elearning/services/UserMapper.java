package com.jonnie.elearning.services;

import com.jonnie.elearning.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
}
