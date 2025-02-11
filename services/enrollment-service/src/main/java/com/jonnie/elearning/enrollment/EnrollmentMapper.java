package com.jonnie.elearning.enrollment;

import com.jonnie.elearning.openfeign.course.CourseEnrollResponse;
import com.jonnie.elearning.openfeign.course.CourseResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentMapper {
    public Enrollment toEnrollment(EnrollmentConfirmation enrollmentRequest) {
        return Enrollment.builder()
                .courseIds(enrollmentRequest.courseIds())
                .userId(enrollmentRequest.userId())
                .isPaid(enrollmentRequest.isPaid())
                .paymentMethod(enrollmentRequest.paymentMethod())
                .instructorIds(enrollmentRequest.instructorIds())
                .build();
    }

    public List<EnrollmentResponse> toEnrollmentResponse(List<Enrollment> content,
                                                         List<CourseEnrollResponse> courses) {
        return content.stream()
                .flatMap(enrollment -> enrollment.getCourseIds().stream()
                        .map(courseId -> {
                            CourseEnrollResponse courseEnrollResponse = courses.stream()
                                    .filter(course -> course.getCourseId().equals(courseId))
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("Course not found"));

                            return new EnrollmentResponse(
                                    enrollment.getEnrollmentId(),
                                    courseEnrollResponse
                            );

                        }))
                .collect(Collectors.toList());

    }
}
