package com.jonnie.elearning.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Document
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private boolean isActive;
    private ROLE role;
}
