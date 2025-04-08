package com.jonnie.elearning.recommendation;


import com.jonnie.elearning.recommendation.requests.KnowYouRequest;
import com.jonnie.elearning.recommendation.responses.KnowYouResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class KnowYouMapper {
    public KnowYou mapToKnowYou(@Valid KnowYouRequest knowYouRequest) {
        return KnowYou.builder()
                .interestedTags(knowYouRequest.interestedTags())
                .interestedCategory(knowYouRequest.interestedCategory())
                .preferredSkillLevel(knowYouRequest.preferredSkillLevel())
                .learningGoal(knowYouRequest.learningGoal())
                .build();
    }

    public KnowYouResponse mapToKnowYouResponse(KnowYou knowYou) {
        return KnowYouResponse.builder()
                .knowYouId(knowYou.getKnowYouId())
                .userId(knowYou.getUserId())
                .interestedTags(knowYou.getInterestedTags())
                .interestedCategory(knowYou.getInterestedCategory())
                .preferredSkillLevel(knowYou.getPreferredSkillLevel())
                .learningGoal(knowYou.getLearningGoal())
                .build();
    }
}
