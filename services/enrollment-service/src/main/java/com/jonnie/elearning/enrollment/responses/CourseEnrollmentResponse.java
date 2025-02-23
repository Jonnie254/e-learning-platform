package com.jonnie.elearning.enrollment.responses;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseEnrollmentResponse {
    private String courseId;
    private String courseName;
    private String courseUrlImage;
    private String InstructorName;
    private int enrollentCount;
}
