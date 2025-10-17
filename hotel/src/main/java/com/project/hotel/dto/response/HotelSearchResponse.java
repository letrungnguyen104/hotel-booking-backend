package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelSearchResponse {
    int id;
    String address;
    String name;
    String city;
    String country;
    String amenities;
    Double oldPrice;
    Double newPrice;
    Double stars;
    Long reviewCount;
    String image;
}
