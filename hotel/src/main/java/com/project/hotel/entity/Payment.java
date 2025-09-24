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
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    Booking booking;

    @Column(name = "amount")
    Double amount;

    @Column(name = "method")
    String method;

    @Column(name = "transaction_id")
    String transactionId;

    @Column(name = "status")
    String status;

    @Column(name = "paid_at")
    LocalDateTime paidAt;
}