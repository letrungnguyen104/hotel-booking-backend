// src/main/java/com/project/hotel/dto/response/AdminDashboardDataResponse.java
package com.project.hotel.dto.response;

import com.project.hotel.enums.HotelStatus;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AdminDashboardDataResponse {

    private double totalRevenue;
    private long totalBookings;
    private long totalHotels;
    private long totalUsers;
    private List<DailyRevenue> revenueOverTime;
    private List<RoleDistribution> userRoleDistribution;
    private List<HotelRevenue> topHotelsByRevenue;
    private List<SimpleHotelInfo> hotelOverview;

    @Data
    @AllArgsConstructor
    public static class DailyRevenue {
        private LocalDate date;
        private double revenue;

        public DailyRevenue(java.sql.Date date, Double revenue) {
            this.date = (date != null) ? date.toLocalDate() : null;
            this.revenue = (revenue != null) ? revenue : 0.0;
        }
    }

    @Data
    @AllArgsConstructor
    public static class RoleDistribution {
        private String type;
        private long value;
    }

    @Data
    @AllArgsConstructor
    public static class HotelRevenue {
        private String hotel;
        private double revenue;
    }

    @Data
    @AllArgsConstructor
    public static class SimpleHotelInfo {
        private int id;
        private String name;
        private String owner;
        private HotelStatus status;
    }

}