package com.jonnie.elearning.message;


import com.jonnie.elearning.messagestatus.MessageStatusResponse;
import com.jonnie.elearning.openfeign.user.AuthenticationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MessageMapper {
    private final AuthenticationClient authenticationClient;
    public MessageResponse toMessageResponse(Message message) {
        String senderName = authenticationClient.getUserSenderNames(message.getSenderId());
        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .type(message.getMessageType())
                .mediaFilePath(message.getMediaFilePath())
                .senderName(senderName)
                .createdAt(message.getCreatedAt())
                .messageStatuses(message.getMessageStatuses().stream()
                        .map(status -> MessageStatusResponse.builder()
                                .messageStatusId(status.getMessageStatusId())
                                .status(status.getStatus())
                                .recepientId(status.getRecepientId())
                                .build())
                        .toList())
                .build();
    }
}

