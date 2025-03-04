package com.jonnie.elearning.chat;

import com.jonnie.elearning.message.Message;
import com.jonnie.elearning.message.MessageType;
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

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<Message> messages;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

}
