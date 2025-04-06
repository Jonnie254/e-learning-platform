package com.jonnie.elearning.recommendation;


import com.jonnie.elearning.recommendation.responses.TagResponse;
import com.jonnie.elearning.utils.SkillLevel;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Document
public class KnowYou {
    @Id
    private String knowYouId;
    private String userId;
    private List<TagResponse> interestedTags;
    private String interestedCategory;
    private SkillLevel preferredSkillLevel;
    private String learningGoal;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;
}
