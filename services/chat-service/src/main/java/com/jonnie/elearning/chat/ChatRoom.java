package com.jonnie.elearning.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String chatRoomId;
    private String courseId;

    @Column(name="participants_id")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "participants", joinColumns = @JoinColumn(name = "chat_room_id"))
    private List<String> participants;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;
}
