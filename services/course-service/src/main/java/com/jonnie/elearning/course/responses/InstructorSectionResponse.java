package com.jonnie.elearning.course.responses;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstructorSectionResponse {
    private String sectionId;
    private String sectionName;
    private String courseName;
    private String sectionDescription;
    private String pdfUrl;
    private String videoUrl;
}
