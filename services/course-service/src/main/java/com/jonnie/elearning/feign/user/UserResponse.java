package com.jonnie.elearning.feign.user;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public String profilePicUrl;

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
