package com.jonnie.elearning.repositories;

import com.jonnie.elearning.progress.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, String> {
    Optional<UserProgress> findByUserIdAndCourseId(String userId, String courseId);


    @Query("SELECT u.progress FROM UserProgress u WHERE u.userId = :userId AND u.courseId = :courseId")
    Optional<BigDecimal> checkCourseProgress(@Param("userId") String userId, @Param("courseId") String courseId);
}
