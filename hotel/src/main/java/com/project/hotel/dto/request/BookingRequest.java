package com.project.hotel.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequest {
    Integer hotelId;
    LocalDate checkInDate;
    LocalDate checkOutDate;
//    Double totalPrice;
    String promotionCode;
    List<RoomBookingDetail> roomsToBook;
    CustomerInfo customerInfo;

    @Data
    public static class RoomBookingDetail {
        private Integer roomTypeId;
        private Integer quantity;
        private List<Integer> services;
    }

    @Data
    public static class CustomerInfo {
        private String fullName;
        private String email;
        private String phoneNumber;
    }
}