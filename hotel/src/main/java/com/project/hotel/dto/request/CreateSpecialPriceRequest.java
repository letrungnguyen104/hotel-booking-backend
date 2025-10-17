package com.project.hotel.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateSpecialPriceRequest {
    int roomTypeId;
    LocalDate startDate;
    LocalDate endDate;
    Double price;
}
