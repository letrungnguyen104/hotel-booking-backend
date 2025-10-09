package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomWithTypeResponse {
    String roomNumber;
    String status;
    int floor;

    String roomTypeName;
    int capacity;
    double pricePerNight;
}