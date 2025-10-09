package com.project.hotel.dto.request;

import com.project.hotel.enums.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoomRequest {
    int floor;
    String roomNumber;
    RoomStatus status;
}