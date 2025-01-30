package com.jonnie.elearning.enrollment;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String enrollmentId;
    private String userId;
    private String courseId;
    private BigDecimal price;
    private String instructorId;
    private boolean isPaid;
    private String paymentMethod;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;
}