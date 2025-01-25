package com.jonnie.elearning.role;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private String id;
    private ROLE role;
    private RoleRequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private String userEmail;
    private String userId;
}
