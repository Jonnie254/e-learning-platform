package com.jonnie.elearning.notification;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Notification notification) {
        log.info("Sending WS notification with payload: {}", notification);
        String groupDestination = "/topic/chat." + notification.getChatRoomId();
        log.info("Sending group notification to: {}", groupDestination);

        messagingTemplate.convertAndSend(groupDestination, notification);
    }
}
