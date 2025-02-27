package com.jonnie.elearning.notification;


import com.jonnie.elearning.message.MessageType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    private String content;
    private String senderId;
    private List<String> receiverIds;
    private String chatRoomId;
    private MessageType messageType;
    private NotificationType notificationType;
    private String mediaUrl;
}
