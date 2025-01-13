package com.jonnie.elearning.services;

import com.jonnie.elearning.user.RoleRequest;
import com.jonnie.elearning.user.RoleRequestStatus;

public record UserRoleRequest(
        String id,
        RoleRequest roleRequest,
        RoleRequestStatus status
) {
}
