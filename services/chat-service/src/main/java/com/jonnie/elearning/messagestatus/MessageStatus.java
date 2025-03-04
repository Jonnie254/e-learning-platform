package com.jonnie.elearning.messagestatus;


import com.jonnie.elearning.message.Message;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "message_statuses")
@EntityListeners(AuditingEntityListener.class)
public class MessageStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String  messageStatusId;
    @ManyToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    private String recepientId;
    @Enumerated(EnumType.STRING)
    private MessageStatusType status;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;
}
