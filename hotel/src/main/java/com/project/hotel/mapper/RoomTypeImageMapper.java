package com.project.hotel.mapper;

import com.project.hotel.entity.RoomTypeImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomTypeImageMapper {
    default String toUrl(RoomTypeImage image) {
        return image.getUrl();
    }
}

