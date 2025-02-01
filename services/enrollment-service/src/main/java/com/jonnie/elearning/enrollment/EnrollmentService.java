package com.jonnie.elearning.enrollment;


import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.openfeign.course.CourseClient;
import com.jonnie.elearning.repositories.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;
    private final EnrollmentMapper enrollmentMapper;


//    public void finalizeEnrollment(EnrollmentConfirmation enrollmentRequest) {
//        //check whether the user has already enrolled for the course
//        boolean alreadyEnrolled = enrollmentRepository.existsByUserIdAndCourseIds(
//                enrollmentRequest.userId(), String.valueOf(enrollmentRequest.courseIds()));
//        if (alreadyEnrolled) {
//            throw new BusinessException("User has already enrolled for the course");
//        }
//        // check a new enrollment
//        Enrollment enrollment = enrollmentMapper.toEnrollment(enrollmentRequest);
//        enrollmentRepository.save(enrollment);
//        log.info("Enrollment successful for user: {} in course: {}", enrollmentRequest.userId(), enrollmentRequest.courseIds());
//    }
}
