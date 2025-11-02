// src/main/java/com/project/hotel/dto/response/ReportResponse.java
package com.project.hotel.dto.response;

import com.project.hotel.enums.ReportStatus;
import com.project.hotel.enums.ReportType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResponse {
    Integer id;
    UserInfo reporterUser;
    UserInfo reportedUser;
    HotelInfo reportedHotel;
    ReportType reportType;
    String reason;
    String details;
    ReportStatus status;
    LocalDateTime createdAt;

    @Data @Builder
    public static class UserInfo {
        private int id;
        private String username;
        private String fullName;
    }

    @Data @Builder
    public static class HotelInfo {
        private int id;
        private String name;
    }
}