package com.project.hotel.entity;

import com.project.hotel.enums.PaymentMethod;
import com.project.hotel.enums.PaymentStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "method")
    PaymentMethod method;

    @Column(name = "transaction_id")
    String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    PaymentStatus status;

    @Column(name = "paid_at")
    LocalDateTime paidAt;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}