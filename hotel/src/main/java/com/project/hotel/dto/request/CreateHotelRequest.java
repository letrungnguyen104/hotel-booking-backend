package com.project.hotel.dto.request;

import com.project.hotel.enums.HotelStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateHotelRequest {
    int ownerId;
    String name;
    String address;
    String city;
    String country;
    String phone;
    String description;
    HotelStatus status;
}