package com.jonnie.elearning.course;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String tagId;
    @Column(unique = true, nullable = false)
    private String tagName;
    @CreatedDate
    private String CreatedAt;
    @LastModifiedDate
    private String updatedAt;
}
