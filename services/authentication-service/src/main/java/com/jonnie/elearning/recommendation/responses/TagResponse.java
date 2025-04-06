package com.jonnie.elearning.recommendation.responses;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagResponse {
    private String tagId;
    private String tagName;
}
