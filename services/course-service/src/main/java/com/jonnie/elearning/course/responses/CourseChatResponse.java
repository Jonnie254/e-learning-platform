package com.jonnie.elearning.course.responses;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseChatResponse {
    private String courseId;
    private String courseName;
    private String courseImageUrl;
    private String InstructorName;
}
