package com.jonnie.elearning;

import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.InvalidRoleRequestException;
import com.jonnie.elearning.openfeign.enrollment.EnrollmentClient;
import com.jonnie.elearning.role.RoleResponse;
import com.jonnie.elearning.role.UserRoleRequest;
import com.jonnie.elearning.services.AuthenticationResponse;
import com.jonnie.elearning.services.UserService;
import com.jonnie.elearning.user.UserAuthenticationRequest;
import com.jonnie.elearning.user.UserRegistrationRequest;
import com.jonnie.elearning.user.UserResponse;
import com.jonnie.elearning.user.UserUpdateRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final EnrollmentClient enrollmentClient;


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


    // method to upload a profile picture
    @PostMapping(value = "/update-profile-picture", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam("file") MultipartFile file
    ) {
        userService.uploadProfilePicture(userId, file);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile picture uploaded successfully");
        return ResponseEntity.accepted().body(response);
    }

    // method to request to become an instructor
    @PostMapping("/request-instructor")
    public ResponseEntity<Map<String, String>> requestInstructor(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid UserRoleRequest userRoleRequest
    ) {
        try {
            boolean hasEnrollments = enrollmentClient.hasEnrollments(userId);
            if (hasEnrollments) {
                return ResponseEntity.badRequest().body(Map.of("error",
                        "Since you've purchased courses using this account, you are restricted from becoming an instructor." +
                        " Please contact support for assistance or consider using a different account for instructor access."));
            }
            userService.requestInstructor(userId, userRoleRequest);
            return ResponseEntity.accepted().build();
        } catch (InvalidRoleRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    //get user details via the id
    @GetMapping("/user/{user-id}")
    public ResponseEntity<UserResponse> getUserDetailsById(
            @PathVariable("user-id") String userId
    ) {
        return ResponseEntity.ok(userService.getUserDetails(userId));
    }
    //get the user details
    @GetMapping("/user-details")
    public ResponseEntity<UserResponse> getUserDetails(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(userService.getUserDetails(userId));
    }

    //get all users
    @GetMapping("/all-active-students")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(name="page", defaultValue = "0", required = false) int page,
            @RequestParam(name="size", defaultValue = "10", required = false) int size
    ) {
        if(!"ADMIN".equalsIgnoreCase(userRole)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.getAllActiveStudents(page, size));
    }
    //get the user name
    @PostMapping("/username/{userId}")
    public String getUserSenderName(@PathVariable("userId") String userId) {
        return userService.getUserSenderNames(userId);
    }
    //get all the instructors
    @GetMapping("/all-active-instructors")
    public ResponseEntity<PageResponse<UserResponse>> getAllInstructors(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(name="page", defaultValue = "0", required = false) int page,
            @RequestParam(name="size", defaultValue = "10", required = false) int size
    ) {
        if(!"ADMIN".equalsIgnoreCase(userRole)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.getAllActiveInstructors(page, size));
    }

    //check if the user has requested to be an instructor
    @GetMapping("/has-requested-role/{userId}")
    public ResponseEntity<Boolean> hasRequestedToBeInstructor(
            @PathVariable String userId) {
        return ResponseEntity.ok(userService.hasRequestedToBeInstructor(userId));
    }
    //get all the role requests
    @GetMapping("/all-role-requests-pending")
    public  ResponseEntity<PageResponse<RoleResponse>> getAllRoleRequests(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(name="page", defaultValue = "0", required = false) int page,
            @RequestParam(name="size", defaultValue = "10", required = false) int size
    ) {
        if(!"ADMIN".equalsIgnoreCase(userRole)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.getAllRoleRequests(page, size));
    }
    @GetMapping("/all-role-requests-rejected")
    public  ResponseEntity<PageResponse<RoleResponse>> getAllRejectedRoleRequests(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(name="page", defaultValue = "0", required = false) int page,
            @RequestParam(name="size", defaultValue = "10", required = false) int size
    ) {
        if(!"ADMIN".equalsIgnoreCase(userRole)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.getAllRejectedRoleRequests(page, size));
    }

    @PutMapping("/process-role-request")
    public ResponseEntity<Map<String, String>> processRoleRequest(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(name = "user-id") String userId,
            @RequestParam(name = "request-id") String requestId,
            @RequestParam(name = "action") String action
    ) {
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access denied. Only admins can process role requests."));
        }
        Map<String, String> response = new HashMap<>();
        switch (action.toLowerCase()) {
            case "approve":
                userService.approveRoleRequest(requestId, userId);
                response.put("message", "Role request approved successfully");
                return ResponseEntity.ok(response);
            case "disapprove":
                userService.disapproveRoleRequest(requestId, userId);
                response.put("message", "Role request disapproved successfully");
                return ResponseEntity.ok(response);
            default:
                response.put("error", "Invalid action. Valid actions are 'approve' or 'disapprove'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //method to deactivate a user
    @PutMapping("/deactivate-user")
    public ResponseEntity<Void> deactivateUser(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        // Enforce role-based authorization
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Proceed with the deactivation
        userService.deactivateUser(userId);
        return ResponseEntity.ok().build();
    }

    // method to update the user's details
    @PutMapping("/update-user")
    public ResponseEntity<Map<String, String>> updateUser(
            @RequestHeader("X-User-Id") String userId,
            @ModelAttribute @Valid UserUpdateRequest userUpdateRequest,
            @RequestParam(name = "profilePic", required = false) MultipartFile file
    ) {
        try {
            userService.updateUser(userId, userUpdateRequest, file);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User details updated successfully");
            return ResponseEntity.accepted().body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}