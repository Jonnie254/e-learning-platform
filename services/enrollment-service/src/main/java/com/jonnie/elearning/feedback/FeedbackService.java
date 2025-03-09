package com.jonnie.elearning.feedback;


import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.repositories.FeedbackRepository;
import com.jonnie.elearning.repositories.UserProgressRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserProgressRepository userProgressRepository;
    private final FeedbackMapper feedbackMapper;

    public RatingResponse isCourseRated(String userId, String courseId) {
        //check whether the user has rated the course
        boolean isRated = feedbackRepository.existsByUserIdAndCourseId(userId, courseId);
        //get the user progress
        BigDecimal progress = userProgressRepository.checkCourseProgress(userId, courseId)
                .orElse(BigDecimal.ZERO);
        return feedbackMapper.toRatingResponse(progress, isRated);
    }

    public void createFeedback(String userId, String courseId, @Valid FeedbackRequest feedbackRequest) {
        if (feedbackRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new BusinessException("Feedback already exists for this course by the user");
        }
        Feedback feedback = feedbackMapper.toFeedback(feedbackRequest);
        feedback.setUserId(userId);
        feedback.setCourseId(courseId);
        feedbackRepository.save(feedback);
    }
}
