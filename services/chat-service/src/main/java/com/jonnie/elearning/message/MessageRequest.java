package com.jonnie.elearning.message;

public record MessageRequest(
        String content,
        MessageType type,
        String chatRoomId
) {

}
