package com.jonnie.elearning.course;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sectionId;
    private String sectionName;
    private String sectionDescription;
    private String pdfUrl;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @CreatedDate
    @Column(updatable = false, columnDefinition = "TIMESTAMP(6) WITHOUT TIME ZONE")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false, columnDefinition = "TIMESTAMP(6) WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;
}

