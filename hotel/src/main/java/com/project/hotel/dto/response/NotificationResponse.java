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
    int id;
    String title;
    String message;
    String type;
    String status;
    LocalDateTime createdAt;
    String link;
}