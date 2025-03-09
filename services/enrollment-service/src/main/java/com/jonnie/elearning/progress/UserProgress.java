package com.jonnie.elearning.progress;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String progressId;
    private String userId;
    private String courseId;

    @OneToMany(mappedBy = "userProgress", cascade = CascadeType.ALL)
    private List<SectionProgress> sectionProgresses;

    private BigDecimal progress;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;
}
