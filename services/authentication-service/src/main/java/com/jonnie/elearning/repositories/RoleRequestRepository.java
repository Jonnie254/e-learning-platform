package com.jonnie.elearning.repositories;

import com.jonnie.elearning.role.RoleRequest;
import com.jonnie.elearning.role.RoleRequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRequestRepository extends MongoRepository<RoleRequest, String> {
    boolean existsByUserIdAndStatus(String userId, RoleRequestStatus roleRequestStatus);
}
