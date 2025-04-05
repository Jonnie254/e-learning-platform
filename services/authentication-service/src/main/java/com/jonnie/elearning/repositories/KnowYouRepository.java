package com.jonnie.elearning.repositories;

import com.jonnie.elearning.recommendation.KnowYou;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface KnowYouRepository extends MongoRepository<KnowYou,String> {
    Optional<KnowYou> findByUserId(String userId);
}
