package com.jonnie.elearning.user;


import com.jonnie.elearning.role.ROLE;
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
    public ROLE role;
    public String profilePicUrl;
}
