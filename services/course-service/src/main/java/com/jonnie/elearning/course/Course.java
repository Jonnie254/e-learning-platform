package com.jonnie.elearning.course;

import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.tag.Tag;
import com.jonnie.elearning.utils.SkillLevel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String courseId;
    @Column(columnDefinition = "TEXT")
    private String courseName;
    @Column(columnDefinition = "TEXT")
    private String courseUrlImage;
    private String instructorId;
    private String instructorName;
    private BigDecimal price;
    private boolean isPublished;
    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> whatYouWillLearn;
    @Column(columnDefinition = "TEXT")
    private String description;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Section> sections;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "course_tags",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )

    private List<Tag> tags;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @CreatedDate
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP(6) WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false, columnDefinition = "TIMESTAMP(6) WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;
}