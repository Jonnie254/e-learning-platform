package com.jonnie.elearning.feedback;


import com.jonnie.elearning.openfeign.user.UserProfileResponse;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
}
