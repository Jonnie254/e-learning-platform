package com.jonnie.elearning.course.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TotalCoursesResponse {
    private long totalCourses;

}
