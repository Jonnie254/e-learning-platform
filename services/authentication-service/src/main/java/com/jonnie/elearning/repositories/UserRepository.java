package com.jonnie.elearning.repositories;


import com.jonnie.elearning.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query("{ 'isActive': true, 'role': 'STUDENT' }")
    Page<User> findAllActiveStudents(Pageable pageable);


    @Query("{ 'isActive': true, 'role': 'INSTRUCTOR' }")
    Page<User> findAllActiveInstructors(Pageable pageable);
}
