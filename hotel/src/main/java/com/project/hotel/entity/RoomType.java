package com.project.hotel.entity;

import com.project.hotel.enums.RoomTypeStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "room_type")
public class RoomType {
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

    @Column(name = "capacity")
    int capacity;

    @Column(name = "price_per_night")
    Double pricePerNight;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    RoomTypeStatus status;

    @OneToMany(mappedBy = "roomType")
    Set<Room> rooms;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<RoomTypeImage> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "room_type_amenity",
            joinColumns = @JoinColumn(name = "room_type_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    List<Amenity> amenities = new ArrayList<>();

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialPrice> specialPrices = new ArrayList<>();
}