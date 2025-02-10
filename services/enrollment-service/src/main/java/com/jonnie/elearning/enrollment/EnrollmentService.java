package com.jonnie.elearning.enrollment;


import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.openfeign.course.CourseClient;
import com.jonnie.elearning.repositories.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;
    private final EnrollmentMapper enrollmentMapper;

    public List<String> getEnrolledCourses(String userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream()
                .flatMap(enrollment -> enrollment.getCourseIds().stream())
                .toList();
    }
}
