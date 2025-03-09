package com.jonnie.elearning.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
        @NotNull(message= "Comment is required")
        @NotBlank(message = "Comment is required")
        String comment,
        @NotNull(message = "Rating is required")
        @Min(message = "Rating must be greater than 0", value = 1)
        @Max(message = "Rating cannot be greater than 5", value = 5)
        double rating
) {
}
