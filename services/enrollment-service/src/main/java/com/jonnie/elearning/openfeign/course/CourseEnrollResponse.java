package com.jonnie.elearning.openfeign.course;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseEnrollResponse {
    private String courseId;
    private String courseName;
    private String courseUrlImage;
    private String InstructorName;
}
