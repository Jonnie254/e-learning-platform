package com.jonnie.elearning.enrollment.responses;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CourseRatingResponse {
    private String courseId;
    private double rating;
}
