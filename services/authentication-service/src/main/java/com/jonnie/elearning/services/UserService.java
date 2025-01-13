package com.jonnie.elearning.services;

import com.jonnie.elearning.email.EmailService;
import com.jonnie.elearning.email.EmailTemplateEngine;
import com.jonnie.elearning.exceptions.*;
import com.jonnie.elearning.jwt.JwtService;
import com.jonnie.elearning.repositories.RoleRequestRepository;
import com.jonnie.elearning.repositories.TokenRepository;
import com.jonnie.elearning.repositories.UserRepository;
import com.jonnie.elearning.user.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRequestRepository roleRequestRepository;
    @Value("${application.mailing.frontend.activationUrl}")
    private String activationUrl;
    // method to register a new user
    public void registerUser(@Valid UserRegistrationRequest userRegistrationRequest) throws MessagingException {
        var user = userMapper.toUser(userRegistrationRequest);
        userRepository.save(user);
        sendValidationEmail(user);
    }

    // method to send the validation email
    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateEngine.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken, "Activate your account"
                
        );
    }

    //method to generate and save the activation token
    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .createAt(LocalDateTime.now())
                .expireAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    // method to generate  the activation code
    private String generateActivationCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            codeBuilder.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }

    // method to activate a new user account
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));
        if(LocalDateTime.now().isAfter(savedToken.getExpireAt())){
          sendValidationEmail(savedToken.getUser());
          throw new RuntimeException("Token expired. A new token has been sent to your email.");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setActive(true);
        userRepository.save(user);
        savedToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    //method to update the user's details
    public void updateUser(String userId, String userRole, UserUpdateRequest userUpdateRequest) {
        // Fetch the user from the repository using the extracted userId
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Ensure role-based restrictions, if necessary
        if (!userRole.equals("ADMIN") && userUpdateRequest.role() != null) {
            throw new RuntimeException("Non-admin users cannot update roles.");
        }

        // Map updated fields to the existing user
        var updatedUser = userMapper.toUpdate(existingUser, userUpdateRequest);

        // Save the updated user
        userRepository.save(updatedUser);
    }

    // method to authenticate the user
    public AuthenticationResponse authenticateUser(UserAuthenticationRequest userAuthenticationRequest) {
        log.info("Authentication request received for email: {}", userAuthenticationRequest.email());

        // Check if the user exists in the database
        var user = userRepository.findByEmail(userAuthenticationRequest.email())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userAuthenticationRequest.email());
                    return new UserNotFoundException("User not found");
                });

        // Check if the user is active
        if (!user.isActive()) {
            throw new InactiveUserException("User is not active");
        }

        // Check if the provided password matches the stored one
        boolean passwordMatches = passwordEncoder.matches(userAuthenticationRequest.password(), user.getPassword());
        if (!passwordMatches) {
            log.error("Invalid credentials for email: {}", userAuthenticationRequest.email());
            throw new InvalidCredentialsExceptions("Invalid credentials");
        }

        // Generate JWT token
        var claims = new HashMap<String, Object>();
        claims.put("fullname", user.getFullName());
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    // method to deactivate a user
    public void deactivateUser(String userId) {
        // Fetch the user from the repository using the extracted userId
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        // Deactivate the user
        existingUser.setActive(false);
        // Save the updated user
        userRepository.save(existingUser);
    }

    //method to request to become an instructor
    // Method to request to become an instructor
    public void requestInstructor(String userId, @Valid UserRoleRequest userRoleRequest) {
        // Fetch the user from the repository using the provided userId
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Check if the requested role is 'INSTRUCTOR'
        if (userRoleRequest.roleRequest().getRequestedRole() != ROLE.INSTRUCTOR) {
            throw new InvalidRoleRequestException("Only INSTRUCTOR role can be requested.");
        }

        // Check if a role request for this user already exists
        boolean existingRequest = roleRequestRepository.existsByUserIdAndStatus(userId, RoleRequestStatus.PENDING);
        if (existingRequest) {
            throw new InvalidRoleRequestException("You already have a pending request to become an instructor.");
        }

        // Create a new RoleRequest object with 'PENDING' status
        RoleRequest roleRequest = RoleRequest.builder()
                .user(existingUser)                       // Associate the request with the user
                .requestedRole(ROLE.INSTRUCTOR)           // Set the requested role to INSTRUCTOR
                .status(RoleRequestStatus.PENDING)        // Set the status to PENDING
                .build();

        // Save the RoleRequest to the repository
        roleRequestRepository.save(roleRequest);
    }


}
