package com.jonnie.elearning.repositories;

import com.jonnie.elearning.progress.SectionProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectionProgressRepository extends JpaRepository<SectionProgress, String> {
    Optional<SectionProgress> findByUserProgressUserIdAndSectionId(String userId, String sectionId);
}
