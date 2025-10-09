package com.project.hotel.entity;

import com.project.hotel.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "room_type_id", nullable = false)
    RoomType roomType;

    @Column(name = "room_number")
    String roomNumber;

    @Column(name = "floor")
    int floor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    RoomStatus status;
}
