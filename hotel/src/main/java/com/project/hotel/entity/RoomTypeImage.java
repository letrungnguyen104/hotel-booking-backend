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
@Table(name = "room_image")
public class RoomTypeImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "room_type_id", nullable = false)
    RoomType roomType;

    @Column(name = "url", nullable = false)
    String url;

    @Column(name = "is_main")
    boolean isMain;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}