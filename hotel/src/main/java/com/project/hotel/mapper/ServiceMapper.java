package com.project.hotel.mapper;

import com.project.hotel.dto.request.CreateServiceRequest;
import com.project.hotel.dto.request.UpdateServiceRequest;
import com.project.hotel.dto.response.ServiceResponse;
import com.project.hotel.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookingServices", ignore = true)
    Service toService(CreateServiceRequest request);

    ServiceResponse toServiceResponse(Service service);

    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookingServices", ignore = true)
    void updateServiceFromDto(UpdateServiceRequest dto, @MappingTarget Service entity);
}
