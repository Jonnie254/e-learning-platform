package com.jonnie.elearning.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByChatRoom_ChatRoomId(String chatRoomId);
}
