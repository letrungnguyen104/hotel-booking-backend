package com.project.hotel.entity;

import com.project.hotel.enums.ServiceStatus;
import com.project.hotel.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    Hotel hotel;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "price")
    Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    ServiceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ServiceStatus status;

    @OneToMany(mappedBy = "service")
    Set<BookingService> bookingServices;
}