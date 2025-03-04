package com.jonnie.elearning.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByChatRoom_ChatRoomId(String chatRoomId);

    @Query("SELECT m FROM Message m WHERE" +
            " m.chatRoom.chatRoomId = :chatRoomId " +
            "ORDER BY m.createdAt DESC LIMIT 1")
    Optional<Message> findLastMessageByChatRoomId(@Param("chatRoomId") String chatRoomId);

}
