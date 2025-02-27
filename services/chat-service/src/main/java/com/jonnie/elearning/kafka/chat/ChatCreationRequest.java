package com.jonnie.elearning.kafka.chat;


import java.util.List;

public record ChatCreationRequest(
        String userId,
        List<String> courseIds
) {
}
