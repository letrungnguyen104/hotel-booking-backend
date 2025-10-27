package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    private int conversationPartnerId;
    private String conversationPartnerName;
    private String conversationPartnerUsername;
    private String conversationPartnerAvatar;
    private String lastMessage;
    private String lastMessageSender;
    private LocalDateTime timestamp;
    private long unreadCount;
    private String lastMessageStatus;
    private int hotelId;
    private String hotelName;
}
