package com.jonnie.elearning.course.responses;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectionResponse {
    private String sectionId;
    private String sectionName;
    private String sectionDescription;
    private String pdfUrl;
    private String videoUrl;
    private String courseId;
}
