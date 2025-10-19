package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    private int id;
    private String title;
    private String message;
    private String type;
    private String status;
    private LocalDateTime createdAt;
}