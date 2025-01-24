package com.jonnie.elearning.role;

import com.jonnie.elearning.user.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Document
public class RoleRequest {
    @Id
    private String id;
    private ROLE requestedRole = ROLE.INSTRUCTOR;
    private RoleRequestStatus status = RoleRequestStatus.PENDING;
    private User user;
    @CreatedDate
    private LocalDateTime requestedAt;
    @CreatedDate
    private LocalDateTime approvedAt;

}
