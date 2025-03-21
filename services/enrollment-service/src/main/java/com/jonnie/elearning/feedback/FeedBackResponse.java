package com.jonnie.elearning.feedback;


import com.jonnie.elearning.openfeign.user.UserProfileResponse;
import lombok.*;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FeedBackResponse {
    private String feedbackId;
    private UserProfileResponse userProfileResponse;
    private String courseId;
    private String comment;
    private double rating;
}
