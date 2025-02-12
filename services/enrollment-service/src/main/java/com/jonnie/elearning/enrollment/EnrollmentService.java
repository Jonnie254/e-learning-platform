package com.jonnie.elearning.enrollment;


import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.openfeign.course.CourseClient;
import com.jonnie.elearning.openfeign.course.CourseEnrollResponse;
import com.jonnie.elearning.openfeign.course.CourseResponse;
import com.jonnie.elearning.repositories.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public PageResponse<EnrollmentResponse> getEnrolledCoursesDetails(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Enrollment> enrollments = enrollmentRepository.findByUsersId(userId, pageable);
        List<String> courseIds = enrollments.getContent().stream()
                .flatMap(enrollment -> enrollment.getCourseIds().stream())
                .distinct()
                .toList();
        List<CourseEnrollResponse> courses = courseClient.getCoursesByIds(courseIds);
        List<EnrollmentResponse> enrollmentResponses = enrollmentMapper.toEnrollmentResponse(enrollments.getContent(), courses);
        return PageResponse.<EnrollmentResponse>builder()
                .content(enrollmentResponses)
                .totalPages(enrollments.getTotalPages())
                .totalElements(enrollments.getTotalElements())
                .build();
    }

    public Boolean hasEnrollments(String userId) {
        return enrollmentRepository.existsByUserId(userId);
    }
}
