package com.jonnie.elearning.services;

import com.cloudinary.Cloudinary;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.email.EmailService;
import com.jonnie.elearning.email.EmailTemplateEngine;
import com.jonnie.elearning.exceptions.*;
import com.jonnie.elearning.jwt.JwtService;
import com.jonnie.elearning.repositories.RoleRequestRepository;
import com.jonnie.elearning.repositories.TokenRepository;
import com.jonnie.elearning.repositories.UserRepository;
import com.jonnie.elearning.role.*;
import com.jonnie.elearning.user.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

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
    private final Cloudinary cloudinary;
    @Value("${application.mailing.frontend.activationUrl}")
    private String activationUrl;
    @Value("${application.cloudinary.folder}")
    private String folder;

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
          throw new BusinessException("Token expired. A new token has been sent to your email.");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setActive(true);
        userRepository.save(user);
        savedToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    //method to update the user's details
    public void updateUser(String userId, UserUpdateRequest userUpdateRequest, MultipartFile file) {
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String profileImageUrl = null;
        if (file != null && !file.isEmpty()) {
            profileImageUrl = storeFile(file, userId);
        }
        var updatedUser = userMapper.toUpdate(existingUser, userUpdateRequest, profileImageUrl);
        userRepository.save(updatedUser);
    }


    // method to get all the active users
    public PageResponse<UserResponse> getAllActiveUsers(int page, int size, String userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAllUsersActiveExcludingUser(userId, pageable);
        List<UserResponse> userResponse = users.stream()
                .map(userMapper::toUserResponse)
                .toList();
        return new PageResponse<>(
                userResponse,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isLast(),
                users.isFirst()
        );
    }
    //method to update the user's profile picture
    public String storeFile(MultipartFile file, String id) {
        try {
            var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    new HashMap<>(
                            java.util.Map.of(
                                    "public_id", folder + "/public" + id,
                                    "overwrite", true,
                                    "resource_type", "auto"
                            )
                    ));
            return (String) uploadResult.get("url");
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file");
        }
    }

    // method to get the user details
    public UserResponse getUserDetails(String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toUserResponse(user);
    }

    //method to update the user upload profile picture
    public void uploadProfilePicture(String userId, MultipartFile file) {
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String profilePicUrl = storeFile(file, userId);
        // Update the user's profile picture URL
        existingUser.setProfilePicUrl(profilePicUrl);
        // Save the updated user
        userRepository.save(existingUser);
    }

    // method to authenticate the user
    public AuthenticationResponse authenticateUser(UserAuthenticationRequest userAuthenticationRequest) {
        log.info("Authentication request received for email: {}", userAuthenticationRequest.email());

        // Check if the user exists in the database
        var userOptional = userRepository.findByEmail(userAuthenticationRequest.email());

        if (userOptional.isEmpty()) {
            log.warn("Authentication failed for email: {}", userAuthenticationRequest.email());
            throw new InvalidCredentialsExceptions("Invalid email or password");
        }

        var user = userOptional.get();

        // Check if the user is active
        if (!user.isActive()) {
            throw new InactiveUserException("User is not active");
        }

        // Check if the provided password matches the stored one
        boolean passwordMatches = passwordEncoder.matches(userAuthenticationRequest.password(), user.getPassword());
        if (!passwordMatches) {
            throw new InvalidCredentialsExceptions("Invalid email or password");
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

    // Method to request to become an instructor
    public void requestInstructor(String userId, @Valid UserRoleRequest userRoleRequest) {
        // Fetch the user from the repository using the provided userId
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Check if the requested role is 'INSTRUCTOR'
        if (!userRoleRequest.roleRequest().getRequestedRole().equals(ROLE.INSTRUCTOR)) {
            throw new InvalidRoleRequestException("Only INSTRUCTOR role can be requested.");
        }

        // Check if a role request for this user already exists
        boolean existingRequest = roleRequestRepository.existsByUserIdAndStatus(userId, RoleRequestStatus.PENDING);
        if (existingRequest) {
            throw new InvalidRoleRequestException("You already have a pending request to become an instructor.");
        }
        // Create a new RoleRequest object with 'PENDING' status
        RoleRequest roleRequest = RoleRequest.builder()
                .user(existingUser)
                .requestedRole(ROLE.INSTRUCTOR)
                .status(RoleRequestStatus.PENDING)
                .build();
        // Save the RoleRequest to the repository
        roleRequestRepository.save(roleRequest);
    }

    //method to get all the role requests
    public PageResponse<RoleResponse> getAllRoleRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestedAt").descending());
        Page<RoleRequest> roleRequests = roleRequestRepository.findAll(pageable);
        List<RoleResponse> roleResponses = roleRequests.stream()
                .map(userMapper::toRoleResponse)
                .toList();
        return new PageResponse<>(
                roleResponses,
                roleRequests.getNumber(),
                roleRequests.getSize(),
                roleRequests.getTotalElements(),
                roleRequests.getTotalPages(),
                roleRequests.isLast(),
                roleRequests.isFirst()
        );
    }

    //method to approve a role request
    public void approveRoleRequest(String requestId, String userId) {
        var roleRequest = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new RoleRequestNotFoundException("Role request not found"));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (roleRequest.getStatus() != RoleRequestStatus.PENDING) {
            throw new InvalidRoleRequestException("Role request already approved or rejected");
        }
        roleRequest.setStatus(RoleRequestStatus.APPROVED);
        roleRequest.setApprovedAt(LocalDateTime.now());
        roleRequestRepository.save(roleRequest);
        user.setRole(ROLE.INSTRUCTOR);
        userRepository.save(user);
    }

    //method to reject a role request
    public void disapproveRoleRequest(String requestId, String userId) {
        var roleRequest = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new RoleRequestNotFoundException("Role request not found"));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (roleRequest.getStatus() != RoleRequestStatus.PENDING) {
            throw new InvalidRoleRequestException("Role request already processed");
        }
        roleRequest.setStatus(RoleRequestStatus.REJECTED);
        roleRequest.setApprovedAt(LocalDateTime.now());
        roleRequestRepository.save(roleRequest);
        user.setRole(ROLE.STUDENT);
        userRepository.save(user);
    }
}

