package com.project.hotel.mapper;

import com.project.hotel.dto.request.CreateRoomRequest;
import com.project.hotel.dto.response.RoomResponse;
import com.project.hotel.dto.response.RoomWithTypeResponse;
import com.project.hotel.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "roomTypeId", expression = "java(room.getRoomType() != null ? room.getRoomType().getId() : null)")
    RoomResponse toRoomResponse(Room room);
    Room toRoom(CreateRoomRequest request);

    default RoomWithTypeResponse toRoomWithTypeResponse(Room room) {
        return RoomWithTypeResponse.builder()
                .roomNumber(room.getRoomNumber())
                .status(room.getStatus().name())
                .floor(room.getFloor())
                .roomTypeName(room.getRoomType().getName())
                .capacity(room.getRoomType().getCapacity())
                .pricePerNight(room.getRoomType().getPricePerNight())
                .build();
    }
}
