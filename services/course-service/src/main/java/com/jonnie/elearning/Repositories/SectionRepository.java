package com.jonnie.elearning.Repositories;

import com.jonnie.elearning.course.Course;
import com.jonnie.elearning.course.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SectionRepository extends JpaRepository<Section, String> {


    @Query("SELECT s FROM Section s WHERE s.course = ?1")
    Page<Section> findByCourse(Course course, Pageable pageable);
}
