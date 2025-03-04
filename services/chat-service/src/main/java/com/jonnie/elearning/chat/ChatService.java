package com.jonnie.elearning.chat;

import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.message.MessageRepository;
import com.jonnie.elearning.message.MessageType;
import com.jonnie.elearning.messagestatus.MessageStatusRespository;
import com.jonnie.elearning.openfeign.course.CourseChatResponse;
import com.jonnie.elearning.openfeign.course.CourseClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MessageStatusRespository messageStatusRespository;
    private final CourseClient courseClient;

    public PageResponse<CoursesChatRoomResponse> getChatRooms(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findByParticipantsContaining(userId, pageable);
        Map<String, CourseChatResponse> courseMap = fetchCourseDetails(chatRoomPage);
        List<CoursesChatRoomResponse> chatRooms = chatRoomPage.stream()
                .map(chatRoom -> mapToChatRoomResponse(chatRoom, userId, courseMap))
                .toList();

        return buildPageResponse(chatRooms, chatRoomPage);
    }

    private Map<String, CourseChatResponse> fetchCourseDetails(Page<ChatRoom> chatRoomPage) {
        List<String> courseIds = chatRoomPage.stream()
                .map(ChatRoom::getCourseId)
                .distinct()
                .toList();

        try {
            return courseClient.getCoursesInfo(courseIds).stream()
                    .collect(Collectors.toMap(CourseChatResponse::getCourseId, course -> course));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private CoursesChatRoomResponse mapToChatRoomResponse(ChatRoom chatRoom, String userId, Map<String, CourseChatResponse> courseMap) {
        CourseChatResponse course = courseMap.getOrDefault(
                chatRoom.getCourseId(),
                new CourseChatResponse(chatRoom.getCourseId(), "Unknown Course", "No details available", "Unknown")
        );
        LastMessageResponse lastMessageResponse = messageRepository.findLastMessageByChatRoomId(chatRoom.getChatRoomId())
                .map(message -> new LastMessageResponse(
                        message.getMessageType() == MessageType.TEXT ? message.getContent() : "Media",
                        message.getCreatedAt()
                )).orElse(null);

        long unreadCount = messageStatusRespository.countUnreadMessages(chatRoom.getChatRoomId(), userId);
        return CoursesChatRoomResponse.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .course(course)
                .lastMessageResponse(lastMessageResponse)
                .unreadCount(unreadCount)
                .build();
    }

    private PageResponse<CoursesChatRoomResponse> buildPageResponse(List<CoursesChatRoomResponse> chatRooms, Page<ChatRoom> chatRoomPage) {
        return new PageResponse<>(
                chatRooms,
                (int) chatRoomPage.getTotalElements(),
                chatRoomPage.getTotalPages(),
                chatRoomPage.getNumber(),
                chatRoomPage.getSize(),
                chatRoomPage.isFirst(),
                chatRoomPage.isLast()
        );
    }
}
