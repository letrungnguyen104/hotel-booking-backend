package com.project.hotel.entity;

import com.project.hotel.enums.GuestInquiryStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "guest_inquiry")
public class GuestInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    String fullName;

    @Column(nullable = false)
    String email;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    String message;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    String adminReply;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    GuestInquiryStatus status = GuestInquiryStatus.PENDING;

    @Builder.Default
    @Column(nullable = false)
    LocalDateTime createdAt = LocalDateTime.now();
}