package com.jonnie.elearning.messagestatus;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MessageStatusRespository extends JpaRepository<MessageStatus, String> {

    @Query("SELECT COUNT(ms) FROM MessageStatus ms " +
            "JOIN ms.message m " +
            "WHERE m.chatRoom.chatRoomId = :chatRoomId " +
            "AND ms.recepientId = :userId " +
            "AND ms.status = 'SENT'")
    long countUnreadMessages(@Param("chatRoomId") String chatRoomId, @Param("userId") String userId);

    @Transactional
    @Modifying
    @Query("UPDATE MessageStatus ms SET ms.status = :status " +
            "WHERE ms.message.messageId IN :messageIds " +
            "AND ms.recepientId = :userId " +
            "AND ms.status <> :status")
    void updateStatusForMessages(@Param("messageIds") List<String> messageIds,
                                 @Param("userId") String userId,
                                 @Param("status") MessageStatusType status);

}
