package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    int id;
    int senderId;
    int receiverId;
    String senderUsername;
    String receiverUsername;
    String message;
    String messageType;
    String status;
    LocalDateTime sentAt;
}
