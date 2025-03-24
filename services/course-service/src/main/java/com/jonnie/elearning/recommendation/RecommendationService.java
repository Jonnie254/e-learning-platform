package com.jonnie.elearning.recommendation;

import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.course.Course;
import com.jonnie.elearning.course.responses.CourseDetailsResponse;
import com.jonnie.elearning.course.responses.CourseRecommendationResponse;
import com.jonnie.elearning.course.responses.CourseResponse;
import com.jonnie.elearning.course.services.CourseMapper;
import com.jonnie.elearning.feign.enrollment.CourseRatingResponse;
import com.jonnie.elearning.feign.enrollment.EnrollmentClient;
import com.jonnie.elearning.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecommendationService {

    private final RecommendationClient recommendationClient;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final EnrollmentClient enrollmentClient;

    public PageResponse<CourseRecommendationResponse> getRecommendations(String userId, int page, int size) {
        log.info("User id: {}", userId);
        Map<String, Object> response = recommendationClient.getRecommendations(userId);
        log.info("getRecommendations response: {}", response);

        if (response == null || !response.containsKey("recommendations")) {
            return new PageResponse<>(List.of(), page, size, 0, 0, true, true);
        }

        // Extract recommended course IDs
        Object recommendationsObj = response.get("recommendations");
        List<String> recommendedCourseIds = new ArrayList<>();
        if (recommendationsObj instanceof List<?>) {
            recommendedCourseIds = ((List<?>) recommendationsObj).stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        if (recommendedCourseIds.isEmpty()) {
            return new PageResponse<>(List.of(), page, size, 0, 0, true, true);
        }

        // Fetch courses from DB
        Page<Course> courses = courseRepository.findByCourseIdIn(
                recommendedCourseIds, PageRequest.of(page, size)
        );

        // Fetch ratings from Enrollment Service
        List<CourseRatingResponse> courseRatings = enrollmentClient.getCoursesRating(recommendedCourseIds);

        // Convert List<CourseRatingResponse> to Map<CourseId, Rating>
        Map<String, Double> courseRatingsMap = courseRatings.stream()
                .collect(Collectors.toMap(CourseRatingResponse::getCourseId, CourseRatingResponse::getRating));

        // Map courses to response and add rating
        List<CourseRecommendationResponse> courseResponses = courseMapper.toCourseRecommendationRatingMapper(courses.getContent());

        // Add ratings to each course response
        courseResponses.forEach(courseResponse ->
                courseResponse.setRating(courseRatingsMap.getOrDefault(courseResponse.getCourseId(), 0.0))
        );

        return new PageResponse<>(
                courseResponses,
                page,
                size,
                courses.getTotalElements(),
                courses.getTotalPages(),
                courses.isLast(),
                courses.isFirst()
        );
    }
}


