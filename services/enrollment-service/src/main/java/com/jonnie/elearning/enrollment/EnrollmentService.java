package com.jonnie.elearning.enrollment;


import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.enrollment.responses.CourseEnrollmentResponse;
import com.jonnie.elearning.enrollment.responses.CourseRatingResponse;
import com.jonnie.elearning.enrollment.responses.EnrollmentStatsResponse;
import com.jonnie.elearning.feedback.Feedback;
import com.jonnie.elearning.openfeign.course.CourseClient;
import com.jonnie.elearning.openfeign.course.CourseEnrollResponse;
import com.jonnie.elearning.openfeign.course.CourseResponse;
import com.jonnie.elearning.openfeign.course.CourseResponseRated;
import com.jonnie.elearning.repositories.EnrollmentRepository;
import com.jonnie.elearning.repositories.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;
    private final EnrollmentMapper enrollmentMapper;
    private final FeedbackRepository feedbackRepository;

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

    public EnrollmentStatsResponse getTotalInstructorEnrollments(String userId) {
        long totalEnrollments = enrollmentRepository.countByInstructorIds(userId);
        return EnrollmentStatsResponse.builder()
                .totalEnrollments((int) totalEnrollments)
                .build();
    }

    public EnrollmentStatsResponse getTotalAdminEnrollments() {
        long totalEnrollments = enrollmentRepository.count();
        return EnrollmentStatsResponse.builder()
                .totalEnrollments((int) totalEnrollments)
                .build();
    }

    public PageResponse<CourseEnrollmentResponse> getInstructorsTotalCourseEnrollments(String userId, int page, int size) {
        List<Enrollment> enrollments = enrollmentRepository.findByInstructorIds(userId);
        return getPaginatedCourseEnrollments(enrollments, page, size);
    }

    public PageResponse<CourseEnrollmentResponse> getAdminTotalCourseEnrollments(int page, int size) {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return getPaginatedCourseEnrollments(enrollments, page, size);
    }

    private PageResponse<CourseEnrollmentResponse> getPaginatedCourseEnrollments(List<Enrollment> enrollments, int page, int size) {
        Map<String, Integer> enrollmentsPerCourse = new HashMap<>();
        for (Enrollment enrollment : enrollments) {
            for (String courseId : enrollment.getCourseIds()) {
                enrollmentsPerCourse.merge(courseId, 1, Integer::sum);
            }
        }
        List<String> courseIds = new ArrayList<>(enrollmentsPerCourse.keySet());
        int totalCourses = courseIds.size();
        int totalPages = (int) Math.ceil((double) totalCourses / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalCourses);
        if (fromIndex >= totalCourses) {
            return new PageResponse<>(Collections.emptyList(), page, size, totalCourses, totalPages, true, page == 0);
        }

        List<String> paginatedCourseIds = courseIds.subList(fromIndex, toIndex);
        List<CourseEnrollResponse> courses = Optional.ofNullable(courseClient.getCoursesByIds(paginatedCourseIds))
                .orElse(Collections.emptyList());
        List<CourseEnrollmentResponse> courseEnrollmentResponses = courses.stream()
                .map(course -> new CourseEnrollmentResponse(
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getCourseUrlImage(),
                        course.getInstructorName(),
                        enrollmentsPerCourse.getOrDefault(course.getCourseId(), 0)
                ))
                .toList();
        return new PageResponse<>(
                courseEnrollmentResponses,
                page,
                size,
                totalCourses,
                totalPages,
                toIndex >= totalCourses,
                page == 0
        );
    }

    public PageResponse<CourseResponseRated> getCoursesByRating(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> coursesFeedback = feedbackRepository.findAllSortedByRating(pageable);
        Map<String, Double> ratingsMap = coursesFeedback.stream()
                .collect(Collectors.groupingBy(
                        Feedback::getCourseId,
                        Collectors.averagingDouble(Feedback::getRating)
                ));

        List<String> courseIds = new ArrayList<>(ratingsMap.keySet());
        List<CourseResponseRated> courses = courseClient.getCourseRecommendations(courseIds).stream()
                .map(course -> new CourseResponseRated(
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getPrice(),
                        course.getInstructorId(),
                        course.getInstructorName(),
                        course.getCourseImageUrl(),
                        ratingsMap.getOrDefault(course.getCourseId(), 0.0)
                ))
                .toList();
        return new PageResponse<>(
                courses,
                coursesFeedback.getNumber(),
                coursesFeedback.getSize(),
                coursesFeedback.getTotalElements(),
                coursesFeedback.getTotalPages(),
                coursesFeedback.isLast(),
                coursesFeedback.isFirst()
        );
    }


    public List<CourseRatingResponse> getCoursesRating(List<String> courseIds) {
        List<Feedback> feedbacks = feedbackRepository.findByCourseIdIn(courseIds);
        Map<String, Double> ratingsMap = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        Feedback::getCourseId,
                        Collectors.averagingDouble(Feedback::getRating)
                ));
        return courseIds.stream()
                .map(courseId -> new CourseRatingResponse(courseId, ratingsMap.getOrDefault(courseId, 0.0)))
                .toList();
    }
}
