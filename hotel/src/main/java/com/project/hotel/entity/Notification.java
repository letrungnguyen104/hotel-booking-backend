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
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

    @Column(name = "title")
    String title;

    @Column(name = "message")
    String message;

    @Column(name = "type")
    String type;

    @Column(name = "status")
    String status;

    @Column(name = "createdAt")
    LocalDateTime createdAt;
}