package com.jonnie.elearning;

import com.jonnie.elearning.message.MessageRequest;
import com.jonnie.elearning.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {
    private final MessageService messageService;
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
}
