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
    String name;
    String city;
    String country;
    String amenities;   // chuỗi tiện ích
    Double oldPrice;    // giá cũ (max)
    Double newPrice;    // giá mới (min)
    Double stars;       // trung bình rating
    Long reviewCount;   // số lượng review
}
