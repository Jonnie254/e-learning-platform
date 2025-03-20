package com.jonnie.elearning.repositories;

import com.jonnie.elearning.feedback.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    Boolean existsByUserIdAndCourseId(String userId, String courseId);

    Page<Feedback> findByCourseId(String courseId, Pageable pageable);
}
