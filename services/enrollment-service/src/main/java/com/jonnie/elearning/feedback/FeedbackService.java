package com.jonnie.elearning.feedback;


import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.repositories.FeedbackRepository;
import com.jonnie.elearning.repositories.UserProgressRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

    public PageResponse<FeedBackResponse> getCourseFeedbacks(String courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("CreatedAt").descending());
        Page<Feedback> feedbacks = feedbackRepository.findByCourseId(courseId, pageable);
        List<FeedBackResponse> feedBackResponses= feedbacks.stream()
                .map(feedbackMapper::toFeedBackResponse)
                .toList();
        return new PageResponse<>(
                feedBackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isLast(),
                feedbacks.isFirst()
        );
    }
}
