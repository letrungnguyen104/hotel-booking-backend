package com.project.hotel.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileResponse {
    int id;
    String username;
    String email;
    String fullName;
    String phoneNumber;
    LocalDateTime updatedAt;
    String imgPath;
}
