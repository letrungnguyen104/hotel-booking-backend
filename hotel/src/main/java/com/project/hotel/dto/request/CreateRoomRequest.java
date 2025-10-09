package com.project.hotel.dto.request;

import com.project.hotel.entity.RoomType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoomRequest {
    int roomTypeId;
    int floor;
    String roomNumber;
}
