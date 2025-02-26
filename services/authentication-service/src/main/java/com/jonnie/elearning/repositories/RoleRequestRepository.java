package com.jonnie.elearning.repositories;

import com.jonnie.elearning.role.RoleRequest;
import com.jonnie.elearning.role.RoleRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RoleRequestRepository extends MongoRepository<RoleRequest, String> {

    boolean existsByUserIdAndStatus(String userId, RoleRequestStatus roleRequestStatus);

    @Query("{ 'status': 'PENDING' }")
    Page<RoleRequest> statusPending(Pageable pageable);

    @Query("{ 'status': 'REJECTED' }")
    Page<RoleRequest> statusRejected(Pageable pageable);
}
