package com.jonnie.elearning.feign.enrollment;


import jakarta.persistence.Entity;
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
