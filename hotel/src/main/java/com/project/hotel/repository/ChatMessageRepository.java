package com.project.hotel.repository;

import com.project.hotel.entity.ChatMessage;
import com.project.hotel.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender.id = :senderId AND m.receiver.id = :receiverId) OR " +
            "(m.sender.id = :receiverId AND m.receiver.id = :senderId) " +
            "ORDER BY m.sentAt ASC")
    List<ChatMessage> findChatHistory(@Param("senderId") int senderId, @Param("receiverId") int receiverId);
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.id IN (" +
            "  SELECT MAX(m2.id) FROM ChatMessage m2 " +
            "  WHERE m2.sender.id = :userId OR m2.receiver.id = :userId " +
            "  GROUP BY CASE WHEN m2.sender.id = :userId THEN m2.receiver.id ELSE m2.sender.id END" +
            ") ORDER BY m.sentAt DESC")
    List<ChatMessage> findLastMessageOfEachConversation(@Param("userId") int userId);
    long countByReceiverIdAndStatus(int receiverId, com.project.hotel.enums.MessageStatus status);
    long countBySenderIdAndReceiverIdAndStatus(int senderId, int receiverId, MessageStatus status);
}