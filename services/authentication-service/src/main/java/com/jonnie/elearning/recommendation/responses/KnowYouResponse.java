package com.jonnie.elearning.recommendation.responses;

import com.jonnie.elearning.utils.SkillLevel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KnowYouResponse {
    private String knowYouId;
    private String userId;
    private List<TagResponse> interestedTags;
    private  String interestedCategory;
    private SkillLevel preferredSkillLevel;
    private String learningGoal;
}
