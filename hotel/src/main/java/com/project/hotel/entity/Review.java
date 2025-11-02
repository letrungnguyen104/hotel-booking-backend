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
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

    @ManyToOne
    @JoinColumn(name = "hotelId")
    Hotel hotel;

    @OneToOne
    @JoinColumn(name = "booking_id", unique = true)
    Booking booking;

    @Column(name = "rating")
    int rating;

    @Column(name = "comment")
    String comment;

    @Column(name = "createdAt")
    LocalDateTime createdAt;
}