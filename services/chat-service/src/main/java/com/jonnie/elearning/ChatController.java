package com.jonnie.elearning;

import com.jonnie.elearning.chat.ChatService;
import com.jonnie.elearning.chat.CoursesChatRoomResponse;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.message.MessageRequest;
import com.jonnie.elearning.message.MessageResponse;
import com.jonnie.elearning.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {
    private final MessageService messageService;
    private final ChatService chatService;

    //method to save a message
    @PostMapping("/save-message")
    public ResponseEntity<Map<String, String>> saveMessage(
            @RequestHeader(value = "X-User-Id") String userId,
            @RequestBody MessageRequest message) {
        log.info("Saving message {}", message);
        messageService.saveMessage(message, userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Message saved successfully");
        return ResponseEntity.ok(response);
    }

    //method to get users chatroom
    @GetMapping("/get-chat-rooms")
    public ResponseEntity<PageResponse<CoursesChatRoomResponse>> getChatRoom(
            @RequestHeader(value = "X-User-Id") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        {
            log.info("Getting chat room for user {}", userId);
            return ResponseEntity.ok(chatService.getChatRooms(userId, page, size));

        }
    }

    //method to get messages in a chatroom
    @GetMapping("/get-messages/{chatId}")
    public ResponseEntity<List<MessageResponse>> getAllMessages(
            @PathVariable("chatId") String chatId
    ) {
        return ResponseEntity.ok(messageService.findChatMessages(chatId));
    }


}
