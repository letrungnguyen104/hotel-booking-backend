package com.project.hotel.entity;

import com.project.hotel.enums.WithdrawalStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "withdrawal_request")
public class WithdrawalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    Wallet wallet;

    @Column(nullable = false)
    Double amount;

    @Column(nullable = false)
    String bankName;

    @Column(nullable = false)
    String bankAccountNumber;

    @Column(nullable = false)
    String accountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    WithdrawalStatus status;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}