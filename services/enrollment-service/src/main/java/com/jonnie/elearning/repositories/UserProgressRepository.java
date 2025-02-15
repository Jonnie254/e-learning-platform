package com.jonnie.elearning.repositories;

import com.jonnie.elearning.progress.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, String> {
    Optional<UserProgress> findByUserIdAndCourseId(String userId, String courseId);
}
