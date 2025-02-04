package com.jonnie.elearning.role;

public record UserRoleRequest(
        String id,
        RoleRequest roleRequest,
        RoleRequestStatus status

) {
}
