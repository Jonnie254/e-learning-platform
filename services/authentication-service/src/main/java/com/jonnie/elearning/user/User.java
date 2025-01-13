package com.jonnie.elearning.user;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
    @Indexed(unique = true)
    private String email;
    private String profilePicture;
    private boolean isActive;
    private ROLE role;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
