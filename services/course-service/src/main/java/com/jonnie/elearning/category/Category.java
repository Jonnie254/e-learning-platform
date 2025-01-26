package com.jonnie.elearning.category;

import com.jonnie.elearning.course.Course;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String categoryId;
    public String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Course> courses;
    @CreatedDate
    private String CreatedAt;
    @LastModifiedDate
    private String updatedAt;
}
