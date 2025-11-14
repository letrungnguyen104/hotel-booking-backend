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
@Table(name = "otp_tokens")
public class OtpToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "otp_code", nullable = false)
    String otpCode;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "expire_at", nullable = false)
    LocalDateTime expireAt;

    @Column(name = "verified", nullable = false)
    boolean verified;

    @Column(name = "used", nullable = false)
    boolean used;
}
