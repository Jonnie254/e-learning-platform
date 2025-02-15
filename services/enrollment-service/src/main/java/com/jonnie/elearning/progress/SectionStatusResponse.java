package com.jonnie.elearning.progress;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectionStatusResponse{
    private String sectionId;

    @JsonProperty("isCompleted")
    private boolean isCompleted;
}
