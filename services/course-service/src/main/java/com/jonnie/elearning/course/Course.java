package com.jonnie.elearning.course;

import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.tag.Tag;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private  String courseId;
    private String courseName;
    private String courseUrlImage;
    private String instructorId;
    private String instructorName;
    private BigDecimal price;
    private  String isPublished;
    @Column(columnDefinition = "text[]")
    @ElementCollection
    private List<String> whatYouWillLearn;
    private String description;
    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;
    @ManyToMany
    @JoinTable(
            name = "course_tags",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @CreatedDate
    private String CreatedAt;
    @LastModifiedDate
    private String updatedAt;
}
