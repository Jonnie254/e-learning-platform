package com.jonnie.elearning.repositories;

import com.jonnie.elearning.feedback.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    Boolean existsByUserIdAndCourseId(String userId, String courseId);
}
