package com.project.hotel.entity;

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

    @Column(name = "message")
    String message;

    @Column(name = "messageType")
    String messageType;

    @Column(name = "status")
    String status;

    @Column(name = "sentAt")
    LocalDateTime sentAt;
}