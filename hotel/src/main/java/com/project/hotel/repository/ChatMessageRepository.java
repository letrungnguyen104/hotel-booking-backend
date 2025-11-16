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

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender.id = :senderId AND m.receiver.id = :receiverId) OR " +
            "(m.sender.id = :receiverId AND m.receiver.id = :senderId) " +
            // Thêm điều kiện: chỉ lấy tin nhắn không có hotelId (Admin chat)
            "AND m.hotel.id IS NULL " +
            "ORDER BY m.sentAt ASC")
    List<ChatMessage> findChatHistoryWithoutHotel(
            @Param("senderId") int senderId,
            @Param("receiverId") int receiverId
    );

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "((m.sender.id = :senderId AND m.receiver.id = :receiverId) OR " +
            "(m.sender.id = :receiverId AND m.receiver.id = :senderId)) " +
            "AND m.hotel.id = :hotelId " + // Hàm cũ yêu cầu hotelId
            "ORDER BY m.sentAt ASC")
    List<ChatMessage> findChatHistoryWithHotel(
            @Param("senderId") int senderId,
            @Param("receiverId") int receiverId,
            @Param("hotelId") int hotelId
    );

    @Query(value = """
        WITH RankedMessages AS (
            SELECT *,
                   ROW_NUMBER() OVER(
                       PARTITION BY
                           CASE
                               WHEN sender_id = :userId THEN receiver_id
                               ELSE sender_id
                           END,
                           ISNULL(hotel_id, -1) -- Phân biệt chat có hotelId và không có
                       ORDER BY sent_at DESC
                   ) as rn
            FROM chat_message
            WHERE sender_id = :userId OR receiver_id = :userId
        )
        SELECT *
        FROM RankedMessages
        WHERE rn = 1
        ORDER BY sent_at DESC
        """, nativeQuery = true)
    List<ChatMessage> findLastMessageOfEachConversation(@Param("userId") int userId);

    @Query("SELECT m.sender.id, COUNT(m) FROM ChatMessage m " +
            "WHERE m.receiver.id = :receiverId AND m.status = 'SENT' " +
            "GROUP BY m.sender.id")
    List<Object[]> countUnreadMessagesForUser(@Param("receiverId") int receiverId);

    long countByReceiver_IdAndStatus(int receiverId, com.project.hotel.enums.MessageStatus status);
    long countBySender_IdAndReceiver_IdAndStatus(int senderId, int receiverId, MessageStatus status);
}
