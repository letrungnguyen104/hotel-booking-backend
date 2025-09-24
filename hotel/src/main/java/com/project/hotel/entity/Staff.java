package com.project.hotel.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "hotelId")
    Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

    @Column(name = "role")
    String role;

    @Column(name = "status")
    String status;
}