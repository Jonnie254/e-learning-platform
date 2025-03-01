package com.jonnie.elearning.messagestatus;


import lombok.*;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class MessageStatusResponse {
    private String messageStatusId;
    private String recepientId;
    private MessageStatusType status;
}
