package com.jonnie.elearning.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
}
