package com.project.hotel.mapper;

import com.project.hotel.dto.request.CreateHotelAdminRequest;
import com.project.hotel.dto.response.HotelAdminResponse;
import com.project.hotel.entity.HotelAdminProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface HotelAdminProfileMapper {
    @Mapping(source = "user", target = "owner")
    HotelAdminResponse toHotelAdminResponse(HotelAdminProfile hotelAdminProfile);

    @Mapping(target = "user", ignore = true)
    HotelAdminProfile toHotelAdminProfile(CreateHotelAdminRequest request);

    List<HotelAdminResponse> toListHotelAdminResponse(List<HotelAdminProfile> hotelAdminProfileList);
}
