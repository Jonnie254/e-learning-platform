package com.jonnie.elearning.chat;


import com.jonnie.elearning.openfeign.course.CourseChatResponse;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoursesChatRoomResponse {
    private String chatRoomId;
    private CourseChatResponse course;
}
