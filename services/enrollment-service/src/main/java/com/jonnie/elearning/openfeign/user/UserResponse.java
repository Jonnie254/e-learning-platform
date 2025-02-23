package com.jonnie.elearning.openfeign.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
