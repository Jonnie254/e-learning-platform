package com.jonnie.elearning.recommendation.requests;

import com.jonnie.elearning.utils.SkillLevel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record KnowYouRequest(
        @NotEmpty(message = "Interested tags cannot be empty")
        @Size(min = 3, max = 7, message = "Interested tags must have between 3 and 7 tags")
        List<String> interestedTags,

        @NotEmpty(message = "Interested categories cannot be empty")
        List<String> interestedCategory,

        @NotNull(message = "Preferred skill level cannot be null")
        SkillLevel preferredSkillLevel,

        @NotNull(message = "Learning goal cannot be null")
        String learningGoal
) {
}
