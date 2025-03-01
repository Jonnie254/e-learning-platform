package com.jonnie.elearning.message;

import com.jonnie.elearning.chat.ChatRoom;
import com.jonnie.elearning.chat.ChatRoomRepository;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.messagestatus.MessageStatus;
import com.jonnie.elearning.messagestatus.MessageStatusRespository;
import com.jonnie.elearning.messagestatus.MessageStatusType;
import com.jonnie.elearning.notification.Notification;
import com.jonnie.elearning.notification.NotificationService;
import com.jonnie.elearning.notification.NotificationType;
import com.jonnie.elearning.openfeign.course.CourseChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.eventstream.MessageBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageStatusRespository messageStatusRespository;
    private final NotificationService notificationService;
    private final MessageMapper messageMapper;

    public void saveMessage(MessageRequest messageRequest, String userId) {
        //find chat room
        ChatRoom chatRoom = chatRoomRepository.findById(messageRequest.chatRoomId())
                .orElseThrow(() -> new BusinessException("Chat room not found"));

        //check if the sender Id is part of the chat room
        var newSenderId = chatRoom.getParticipants().stream()
                .filter(participantId -> participantId.equals(userId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("User not part of the chat room"));
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .content(messageRequest.content())
                .senderId(newSenderId)
                .messageType(messageRequest.type())
                .build();
        messageRepository.save(message);
        log.info("Message saved: {}", message);
        //create a message status for each recipient
        List<String> participants = chatRoom.getParticipants();
        participants.stream()
                .filter(participantId -> !participantId.equals(newSenderId))
                .forEach(participantId -> {
                    MessageStatus messageStatus = MessageStatus.builder()
                            .message(message)
                            .recepientId(participantId)
                            .status(MessageStatusType.SENT)
                            .build();
                    messageStatusRespository.save(messageStatus);
                });
        log.info("Message status saved for message: {}", message);
        //send notification to all participants
        Notification notification = Notification.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .notificationType(NotificationType.SEEN)
                .receiverIds(participants.stream()
                        .filter(participantId -> !participantId.equals(newSenderId))
                        .toList())
                .senderId(userId)
                .build();
        notificationService.sendNotification(notification);

    }


    public List<MessageResponse> findChatMessages(String chatId) {
        return messageRepository.findByChatRoom_ChatRoomId(chatId)
                .stream()
                .map(messageMapper::toMessageResponse)
                .toList();
    }
}
