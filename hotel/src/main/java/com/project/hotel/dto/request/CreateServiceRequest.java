package com.project.hotel.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateServiceRequest {
    Integer hotelId;
    String name;
    String description;
    Double price;
    String type;
}