package com.jonnie.elearning.Repositories;

import com.jonnie.elearning.course.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, String> {

}
