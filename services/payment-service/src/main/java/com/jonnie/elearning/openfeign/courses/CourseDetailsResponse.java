package com.jonnie.elearning.openfeign.courses;


import lombok.*;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDetailsResponse {
    private String courseId;
    private String courseName;
    private String courseUrlImage;
}
