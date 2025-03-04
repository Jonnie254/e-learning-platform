package com.jonnie.elearning.chat;


import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LastMessageResponse {
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}

