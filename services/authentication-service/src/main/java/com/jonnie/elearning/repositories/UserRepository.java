package com.jonnie.elearning.repositories;


import com.jonnie.elearning.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
