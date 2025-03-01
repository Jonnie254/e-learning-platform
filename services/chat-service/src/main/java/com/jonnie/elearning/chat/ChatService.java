package com.jonnie.elearning.chat;

import com.jonnie.elearning.common.PageResponse;
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
    private final CourseClient courseClient;

    public PageResponse<CoursesChatRoomResponse> getChatRooms(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        Page<ChatRoom> chatRoomPage = chatRoomRepository.findByParticipantsContaining(userId, pageable);

        List<String> courseIds = chatRoomPage.stream()
                .map(ChatRoom::getCourseId)
                .distinct()
                .toList();

        Map<String, CourseChatResponse> courseMap = new HashMap<>();
        try {
            List<CourseChatResponse> courses = courseClient.getCoursesInfo(courseIds);
            courseMap = courses.stream().collect(
                    Collectors.toMap(CourseChatResponse::getCourseId,
                            course -> course));
        } catch (Exception e) {
            log.error("Failed to fetch course details", e);
        }

        Map<String, CourseChatResponse> finalCourseMap = courseMap;
        List<CoursesChatRoomResponse> chatRooms = chatRoomPage.stream()
                .map(chatRoom -> {
                    CourseChatResponse course = finalCourseMap.getOrDefault(
                            chatRoom.getCourseId(),
                            new CourseChatResponse(chatRoom.getCourseId(), "Unknown Course", "No details available", "Unknown")
                    );
                    return CoursesChatRoomResponse.builder()
                            .chatRoomId(chatRoom.getChatRoomId())
                            .course(course)
                            .build();
                })
                .toList();
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
