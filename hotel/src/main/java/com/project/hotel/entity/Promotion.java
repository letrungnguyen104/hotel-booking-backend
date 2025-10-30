package com.project.hotel.entity;

import com.project.hotel.enums.PromotionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    String description;

    @Column(unique = true)
    String code;

    Double discountPercent;
    Double maxDiscountAmount;
    Double minSpend;

    LocalDate startDate;
    LocalDate endDate;

    String imageUrl;

    @Builder.Default
    boolean isFeatured = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    PromotionStatus status = PromotionStatus.SCHEDULED;
}