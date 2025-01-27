package com.jonnie.elearning.category;

import com.jonnie.elearning.course.Course;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String categoryId;
    @Column(unique = true, nullable = false)
    public String categoryName;
    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Course> courses;
    @CreatedDate
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP(6) WITHOUT TIME ZONE")
    private LocalDateTime CreatedAt;
    @LastModifiedDate
    @Column(insertable = false, columnDefinition = "TIMESTAMP(6) WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;
}
