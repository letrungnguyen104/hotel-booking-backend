package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelTopResponse {
    int id;
    String name;
    String city;
    Double price;
    Double rating;
    Long reviewCount;
    String image;
    Double score;
}