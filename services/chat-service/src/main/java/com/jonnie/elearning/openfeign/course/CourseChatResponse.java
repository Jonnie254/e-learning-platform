package com.jonnie.elearning.openfeign.course;


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
    private String instructorName;
}
