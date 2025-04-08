package com.jonnie.elearning.recommendation.requests;

import com.jonnie.elearning.recommendation.responses.TagResponse;
import com.jonnie.elearning.utils.SkillLevel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateKnowYouRequest(
        String knowYouId,
        @NotEmpty(message = "Interested tags cannot be empty")
        @Size(min = 1, max = 7, message = "Interested tags must have between 1 and 7 tags")
        List<TagResponse> interestedTags,

        @NotEmpty(message = "Interested categories cannot be empty")
        String interestedCategory,

        @NotNull(message = "Preferred skill level cannot be null")
        SkillLevel preferredSkillLevel,

        @NotNull(message = "Learning goal cannot be null")
        String learningGoal
) {

}
