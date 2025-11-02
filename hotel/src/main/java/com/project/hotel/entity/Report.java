package com.project.hotel.entity;

import com.project.hotel.enums.ReportStatus;
import com.project.hotel.enums.ReportType;
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
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "reporter_user_id", nullable = false)
    User reporterUser;

    @ManyToOne
    @JoinColumn(name = "reported_user_id")
    User reportedUser;

    @ManyToOne
    @JoinColumn(name = "reported_hotel_id")
    Hotel reportedHotel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReportType reportType;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    String reason;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    String details;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReportStatus status = ReportStatus.PENDING;

    @Builder.Default
    @Column(nullable = false)
    LocalDateTime createdAt = LocalDateTime.now();
}