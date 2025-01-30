package com.jonnie.elearning.openfeign.user;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
}
