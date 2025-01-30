package com.jonnie.elearning.repositories;

import com.jonnie.elearning.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    boolean existsByUserIdAndCourseId(String userId, String courseId);
}
