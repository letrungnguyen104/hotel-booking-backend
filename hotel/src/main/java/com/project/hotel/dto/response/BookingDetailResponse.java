// src/main/java/com/project/hotel/dto/response/BookingDetailResponse.java
package com.project.hotel.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingDetailResponse {
    private int id;
    private HotelInfo hotel;
    private UserInfo user;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double totalPrice;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private String cancellationReason;
    private List<RoomDetail> rooms;
    private List<ServiceDetail> services;

    @Data
    @Builder
    public static class HotelInfo {
        private int id;
        private String name;
        private String address;
        private String image;
    }

    @Data
    @Builder
    public static class UserInfo {
        private int id;
        private String username;
        private String fullName;
        private String email;
    }

    @Data
    @Builder
    public static class RoomDetail {
        private String roomName;
        private String roomNumber;
        private Double price;
    }

    @Data
    @Builder
    public static class ServiceDetail {
        private String name;
        private Double price;
        private int quantity;
    }
}