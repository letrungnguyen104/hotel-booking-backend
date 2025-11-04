// src/main/java/com/project/hotel/dto/response/DashboardDataResponse.java
package com.project.hotel.dto.response;

import com.project.hotel.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataResponse {
    private LocalDate date;
    private Double amount;
    private BookingStatus status;
    private String roomType;
    private Integer guests;
}