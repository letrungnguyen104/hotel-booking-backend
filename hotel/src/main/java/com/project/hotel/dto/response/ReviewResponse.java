package com.project.hotel.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private int id;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private String fullName;
        private String avatarUrl;
    }
}