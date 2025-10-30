package com.project.hotel.entity;

import com.project.hotel.enums.BookingStatus;
import com.project.hotel.enums.PaymentStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    BookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    PaymentStatus paymentStatus;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "cancellation_reason", columnDefinition = "NVARCHAR(MAX)")
    String cancellationReason;

    @Column(name = "applied_promotion_code")
    String appliedPromotionCode;

    @Column(name = "discount_amount")
    Double discountAmount;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Set<BookingRoom> bookingRooms;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Set<BookingService> bookingServices;
}