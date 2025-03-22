package com.jonnie.elearning.openfeign.user;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
        private String id;
        private String firstName;
        private String lastName;
        private String profileImageUrl;
}
