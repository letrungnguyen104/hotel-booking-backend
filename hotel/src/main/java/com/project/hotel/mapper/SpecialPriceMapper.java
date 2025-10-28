package com.project.hotel.mapper;

import com.project.hotel.dto.response.SpecialPriceResponse;
import com.project.hotel.entity.SpecialPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialPriceMapper {
    @Mapping(source = "roomType.id", target = "roomTypeId")
    SpecialPriceResponse toResponse(SpecialPrice specialPrice);
    List<SpecialPriceResponse> toResponseList(List<SpecialPrice> specialPrices);
}
