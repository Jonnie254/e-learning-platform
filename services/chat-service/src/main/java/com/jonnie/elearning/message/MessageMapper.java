package com.jonnie.elearning.message;


import com.jonnie.elearning.messagestatus.MessageStatus;
import com.jonnie.elearning.messagestatus.MessageStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageMapper {
    public MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .type(message.getMessageType())
                .mediaFilePath(message.getMediaFilePath())
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

