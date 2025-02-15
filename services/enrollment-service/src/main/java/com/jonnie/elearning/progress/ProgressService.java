package com.jonnie.elearning.progress;


import com.jonnie.elearning.openfeign.course.CourseClient;
import com.jonnie.elearning.openfeign.course.CourseSectionsResponse;
import com.jonnie.elearning.repositories.EnrollmentRepository;
import com.jonnie.elearning.repositories.SectionProgressRepository;
import com.jonnie.elearning.repositories.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProgressService {
    private final UserProgressRepository userProgressRepository;
    private final SectionProgressRepository sectionProgressRepository;
    private final CourseClient courseClient;
    private final EnrollmentRepository enrollmentRepository;


    public void initializeUserProgress(String userId, String courseId) {
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
        if (!isEnrolled) {
            return;
        }

        Optional<UserProgress> existingProgress = userProgressRepository.findByUserIdAndCourseId(userId, courseId);
        if (existingProgress.isPresent()) {
            return;
        }
        List<String> sectionIds;
        try {
            sectionIds = courseClient.getCourseSectionIds(courseId).getSectionIds();
        } catch (Exception e) {
            return;
        }

        if (sectionIds.isEmpty()) {
            return;
        }
        // Create UserProgress
        UserProgress userProgress = userProgressRepository.save(
                UserProgress.builder()
                        .userId(userId)
                        .courseId(courseId)
                        .progress(BigDecimal.ZERO)
                        .build()
        );

        List<SectionProgress> sectionProgresses = new ArrayList<>();
        for (String sectionId : sectionIds) {
            sectionProgresses.add(SectionProgress.builder()
                    .sectionId(sectionId)
                    .isCompleted(false)
                    .userProgress(userProgress)
                    .build());
        }
        sectionProgressRepository.saveAll(sectionProgresses);
        userProgress.setSectionProgresses(sectionProgresses);
        userProgressRepository.save(userProgress);
    }

    public SectionStatusResponse getSectionStatus(String userId, String sectionId) {
        Optional<SectionProgress> sectionProgress =
                sectionProgressRepository.findByUserProgressUserIdAndSectionId(userId, sectionId);
        if (sectionProgress.isEmpty()) {
            return SectionStatusResponse.builder()
                    .sectionId(sectionId)
                    .isCompleted(false)
                    .build();
        }
        return SectionStatusResponse.builder()
                .sectionId(sectionId)
                .isCompleted(sectionProgress.get().isCompleted())
                .build();
    }
    public void toggleSectionStatus(String userId, String sectionId) {
        Optional<SectionProgress> sectionProgressOpt =
                sectionProgressRepository.findByUserProgressUserIdAndSectionId(userId, sectionId);
        if (sectionProgressOpt.isEmpty()) {
            return;
        }
        SectionProgress sectionProgress = sectionProgressOpt.get();
        sectionProgress.setCompleted(!sectionProgress.isCompleted());
        sectionProgressRepository.save(sectionProgress);

        // Update User Progress on the Course
        updateUserProgress(userId, sectionProgress.getUserProgress().getCourseId());
    }

    private void updateUserProgress(String userId, String courseId) {
        Optional<UserProgress> userProgressOpt =
                userProgressRepository.findByUserIdAndCourseId(userId, courseId);

        if (userProgressOpt.isEmpty()) {
            return;
        }
        UserProgress userProgress = userProgressOpt.get();
        List<SectionProgress> sections = userProgress.getSectionProgresses();
        long completedSections = sections.stream().filter(SectionProgress::isCompleted).count();
        BigDecimal progressPercentage = BigDecimal.valueOf((double) completedSections / sections.size() * 100);
        userProgress.setProgress(progressPercentage);
        userProgressRepository.save(userProgress);
    }

    public ProgressResponse getProgress(String userId, String courseId) {
        Optional<UserProgress> userProgressOpt = userProgressRepository.findByUserIdAndCourseId(userId, courseId);
        if (userProgressOpt.isEmpty()) {
            return ProgressResponse.builder()
                    .progress(BigDecimal.ZERO)
                    .build();
        }
        return ProgressResponse.builder()
                .progress(userProgressOpt.get().getProgress())
                .build();
    }
}