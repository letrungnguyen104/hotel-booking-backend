package com.project.hotel.mapper;

import com.project.hotel.dto.request.CreateAmenityRequest;
import com.project.hotel.dto.response.AmenityResponse;
import com.project.hotel.entity.Amenity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    AmenityResponse toAmenityResponse(Amenity amenity);
    Amenity toAmenity(CreateAmenityRequest request);
}
