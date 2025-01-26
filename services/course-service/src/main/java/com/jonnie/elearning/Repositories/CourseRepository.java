package com.jonnie.elearning.Repositories;

import com.jonnie.elearning.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository  extends JpaRepository<Course, String> {
}
