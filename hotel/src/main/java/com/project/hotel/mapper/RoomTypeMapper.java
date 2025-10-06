package com.project.hotel.mapper;

import com.project.hotel.dto.request.CreateRoomTypeRequest;
import com.project.hotel.dto.response.RoomTypeResponse;
import com.project.hotel.entity.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { AmenityMapper.class, RoomTypeImageMapper.class })
public interface RoomTypeMapper {
    @Mapping(target = "hotel.id", source = "hotelId")
    @Mapping(target = "status", constant = "ACTIVE")
    RoomType toRoomType(CreateRoomTypeRequest request);

    RoomTypeResponse toRoomTypeResponse(RoomType roomType);
}