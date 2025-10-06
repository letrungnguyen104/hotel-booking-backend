package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelResponse {
    int id;
    OwnerResponse owner;
    String name;
    String address;
    String city;
    String country;
    String phone;
    String description;
    String status;
    LocalDateTime createdAt;
    List<String> images;
    Double rating;
    Long reviewCount;
}