package com.project.hotel.service;
import com.project.hotel.dto.request.CreateServiceRequest;
import com.project.hotel.dto.request.UpdateServiceRequest;
import com.project.hotel.dto.response.ServiceResponse;
import com.project.hotel.entity.Hotel;
import com.project.hotel.entity.Service;
import com.project.hotel.enums.ServiceStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.ServiceMapper;
import com.project.hotel.repository.HotelRepository;
import com.project.hotel.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final HotelRepository hotelRepository;
    private final ServiceMapper serviceMapper;

    public ServiceResponse createService(CreateServiceRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        Service service = serviceMapper.toService(request);
        service.setHotel(hotel);
        service.setStatus(ServiceStatus.ACTIVE);

        Service savedService = serviceRepository.save(service);
        return serviceMapper.toServiceResponse(savedService);
    }

    public List<ServiceResponse> getServicesByHotelForHotelAdmin(Integer hotelId) {
        return serviceRepository.findByHotelId(hotelId).stream()
                .map(serviceMapper::toServiceResponse)
                .collect(Collectors.toList());
    }

    public List<ServiceResponse> getServicesByHotel(Integer hotelId) {
        return serviceRepository.findByHotelId(hotelId).stream()
                .filter(service -> ServiceStatus.ACTIVE.equals(service.getStatus()))
                .map(serviceMapper::toServiceResponse)
                .collect(Collectors.toList());
    }

    public ServiceResponse updateService(Integer serviceId, UpdateServiceRequest request) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        serviceMapper.updateServiceFromDto(request, service);
        Service updatedService = serviceRepository.save(service);
        return serviceMapper.toServiceResponse(updatedService);
    }

    public void deleteService(Integer serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        service.setStatus(ServiceStatus.INACTIVE);
        serviceRepository.save(service);
    }
}