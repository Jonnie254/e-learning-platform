package com.jonnie.elearning.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query("SELECT COUNT(c) > 0 FROM ChatRoom c WHERE :userId" +
            " MEMBER OF c.participants AND c.courseId = :courseId")
    boolean userExistsInChatRoom(String userId, String courseId);
}
