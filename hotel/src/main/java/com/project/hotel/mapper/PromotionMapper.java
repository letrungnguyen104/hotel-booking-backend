package com.project.hotel.mapper;
import com.project.hotel.dto.request.PromotionRequest;
import com.project.hotel.dto.response.PromotionResponse;
import com.project.hotel.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PromotionMapper {
    @Mapping(target = "imageUrl", ignore = true)
    Promotion toPromotion(PromotionRequest request);

    PromotionResponse toPromotionResponse(Promotion promotion);
    List<PromotionResponse> toPromotionResponseList(List<Promotion> promotions);

    @Mapping(target = "imageUrl", ignore = true)
    void updatePromotionFromDto(PromotionRequest dto, @MappingTarget Promotion entity);
}