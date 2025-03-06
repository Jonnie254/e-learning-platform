package com.jonnie.elearning.repositories;

import com.jonnie.elearning.course.Course;
import com.jonnie.elearning.course.Section;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, String> {


    @Query("SELECT s FROM Section s WHERE s.course = ?1")
    Page<Section> findByCourse(Course course, Pageable pageable);

    @Query("SELECT s.sectionId FROM Section s WHERE s.course = :course")
    List<String> findSectionIdsByCourse(@Param("course") Course course);
}
