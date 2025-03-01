package com.jonnie.elearning.message;

import com.jonnie.elearning.messagestatus.MessageStatusResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String messageId;
    private String content;
    private MessageType type;
    private List<MessageStatusResponse> messageStatuses;
    private String senderId;
    private String mediaFilePath;
    private LocalDateTime createdAt;
}
