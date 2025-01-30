package com.jonnie.elearning.progress;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)

public class SectionProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sectionProgressId;
    private String sectionId;
    private String userProgressId;
    private boolean isCompleted;

    @ManyToOne
    private UserProgress userProgress;


    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;


}
