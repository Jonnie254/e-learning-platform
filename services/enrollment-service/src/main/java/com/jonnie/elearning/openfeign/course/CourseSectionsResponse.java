package com.jonnie.elearning.openfeign.course;

import lombok.*;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseSectionsResponse {
    private List<String> sectionIds;
}


