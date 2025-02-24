package com.jonnie.elearning.Repositories;

import com.jonnie.elearning.course.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository  extends JpaRepository<Course, String> {

    @Query("SELECT c FROM Course c WHERE c.isPublished = true")
    Page<Course> findAllPublishedCourses(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.instructorId = :instructorId")
    Page<Course> findAllByInstructorId(String instructorId, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.courseId NOT IN :courseIds AND c.isPublished = true")
    Page<Course> findAllAvailableCourses(List<String> courseIds, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructorId = :instructorId")
    Long countByInstructorId(@Param("instructorId") String instructorId);
}
