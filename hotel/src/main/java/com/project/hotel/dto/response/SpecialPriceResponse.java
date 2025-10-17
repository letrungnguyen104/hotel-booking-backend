package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecialPriceResponse {
    private Integer id;
    private Integer roomTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;
}
