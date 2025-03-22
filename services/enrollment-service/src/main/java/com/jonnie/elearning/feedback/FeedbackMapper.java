package com.jonnie.elearning.feedback;

import com.jonnie.elearning.openfeign.user.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FeedbackMapper {
    public RatingResponse toRatingResponse(BigDecimal progress, boolean isRated) {
        return RatingResponse.builder()
                .isRated(isRated)
                .progress(progress)
                .build();
    }

    public Feedback toFeedback(@Valid FeedbackRequest feedbackRequest) {
        return Feedback.builder()
                .comment(feedbackRequest.comment())
                .rating(feedbackRequest.rating())
                .build();
    }

    public FeedBackResponse toFeedBackResponse(Feedback feedback, UserProfileResponse userProfileResponse) {
        return FeedBackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .courseId(feedback.getCourseId())
                .comment(feedback.getComment())
                .rating(feedback.getRating())
                .userProfileResponse(userProfileResponse)
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
