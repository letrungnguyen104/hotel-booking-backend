// src/main/java/com/project/hotel/entity/ChatMessage.java
package com.project.hotel.entity;
import com.project.hotel.enums.MessageStatus;
import com.project.hotel.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "hotelId")
    Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "senderId")
    User sender;

    @ManyToOne
    @JoinColumn(name = "receiverId")
    User receiver;

    @Column(name = "message", columnDefinition = "NVARCHAR(MAX)")
    String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "messageType")
    MessageType messageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    MessageStatus status;

    @Builder.Default
    @Column(name = "sentAt")
    LocalDateTime sentAt = LocalDateTime.now();
}