package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeAvailabilityResponse {
    int id;
    String name;
    String description;
    int capacity;
    Double pricePerNight;
    String status;
    List<String> images;
    List<AmenityResponse> amenities;
    int availableRoomsCount;
}
