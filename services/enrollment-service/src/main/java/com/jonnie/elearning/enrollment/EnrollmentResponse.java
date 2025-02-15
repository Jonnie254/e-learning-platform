package com.jonnie.elearning.enrollment;


import com.jonnie.elearning.openfeign.course.CourseEnrollResponse;
import com.jonnie.elearning.openfeign.course.CourseResponse;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentResponse {
    private String enrollmentId;
    private CourseEnrollResponse course;
}
