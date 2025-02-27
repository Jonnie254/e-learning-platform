package com.jonnie.elearning.kafka.enrollment;

import com.jonnie.elearning.enrollment.Enrollment;
import com.jonnie.elearning.kafka.chat.ChatCreationProducer;
import com.jonnie.elearning.kafka.chat.ChatCreationRequest;
import com.jonnie.elearning.openfeign.course.CourseClient;
import com.jonnie.elearning.progress.SectionProgress;
import com.jonnie.elearning.progress.UserProgress;
import com.jonnie.elearning.repositories.EnrollmentRepository;
import com.jonnie.elearning.repositories.SectionProgressRepository;
import com.jonnie.elearning.repositories.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentConsumer {
    private final EnrollmentRepository enrollmentRepository;
    private final ChatCreationProducer chatCreationProducer;

    @KafkaListener(topics = "enrollment-topic", groupId = "enrollment-group")
    public void consumeEnrollmentSuccess(EnrollmentConfirmation enrollmentConfirmation) {
        List<String> newCourseIds = enrollmentConfirmation.courseIds();
        for (String courseId : enrollmentConfirmation.courseIds()) {
            // Check if the user is already enrolled in this course
            if (enrollmentRepository.existsByUserIdAndCourseId(enrollmentConfirmation.userId(), courseId)) {
                log.info("Enrollment already exists for user: {} and course: {}. Skipping...",
                        enrollmentConfirmation.userId(), courseId);
                continue;
            }
            // Save the new enrollment
            enrollmentRepository.save(
                    Enrollment.builder()
                            .courseIds(Collections.singletonList(courseId))
                            .userId(enrollmentConfirmation.userId())
                            .isPaid(enrollmentConfirmation.isPaid())
                            .paymentMethod(enrollmentConfirmation.paymentMethod())
                            .instructorIds(enrollmentConfirmation.instructorIds())
                            .build()
            );
            log.info("Enrollment successful for user: {} in course: {}",
                    enrollmentConfirmation.userId(), courseId);
            newCourseIds.add(courseId);
        }

        // Create a new chat for the user
        if (!newCourseIds.isEmpty()) {
            chatCreationProducer.sendChatCreation(new
                    ChatCreationRequest(enrollmentConfirmation.userId(), newCourseIds));
        }
    }
}