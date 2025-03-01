package com.jonnie.elearning.kafka.chat;

import com.jonnie.elearning.chat.ChatRoom;
import com.jonnie.elearning.chat.ChatRoomRepository;
import com.jonnie.elearning.openfeign.course.CourseClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatConsumer {
    public final ChatRoomRepository chatRoomRepository;
    public final CourseClient courseClient;

    @KafkaListener(topics = "chat-topic", groupId = "chat-group")
    @Transactional
    public void consumeChatRoomCreation(ChatCreationRequest chatCreationRequest) {
        List<String> newCourseIds = chatCreationRequest.courseIds();
        for(String courseId : newCourseIds) {
            if (chatRoomRepository.userExistsInChatRoom(chatCreationRequest.userId(), courseId)) {
                log.info("Enrollment already exists for user: {} and course: {}. Skipping...",
                        chatCreationRequest.userId(), courseId);
                continue;
            }
            //get the course instructor id
            String instructorId;
            try {
                instructorId = courseClient.getInstructorId(courseId);
                log.info("Instructor id for course {} is {}", courseId, instructorId);
            } catch (Exception e) {
                log.error("🚨 Failed to get instructor id for course {}. Error: {}", courseId, e.getMessage(), e);
                continue;
            }
            List<String> participants = new ArrayList<>();
            participants.add(chatCreationRequest.userId());
            participants.add(instructorId);
            chatRoomRepository.save(
                    ChatRoom.builder()
                            .courseId(courseId)
                            .participants(participants)
                            .build()
            );
        }
    }
}

