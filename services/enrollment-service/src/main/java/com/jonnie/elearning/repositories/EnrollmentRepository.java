package com.jonnie.elearning.repositories;

import com.jonnie.elearning.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Enrollment e " +
            "JOIN e.courseIds c WHERE e.userId = :userId AND c = :courseId")
    boolean existsByUserIdAndCourseId(String userId, String courseId);

    boolean existsByUserIdAndCourseIds(String userId, List<String> strings);
}
