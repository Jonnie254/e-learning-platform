package com.jonnie.elearning.tag;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagResponse {
    private String tagId;
    private String tagName;
}
