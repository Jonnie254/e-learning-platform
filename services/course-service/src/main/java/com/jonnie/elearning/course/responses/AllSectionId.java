package com.jonnie.elearning.course.responses;

import lombok.*;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllSectionId

 {
    private List<String> sectionIds;

}
