package com.project.hotel.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    Hotel hotel;

    @Column(name = "check_in_date")
    LocalDate checkInDate;

    @Column(name = "check_out_date")
    LocalDate checkOutDate;

    @Column(name = "total_price")
    Double totalPrice;

    @Column(name = "status")
    String status;

    @Column(name = "payment_status")
    String paymentStatus;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "booking")
    Set<BookingRoom> bookingRooms;

    @OneToMany(mappedBy = "booking")
    Set<BookingService> bookingServices;
}