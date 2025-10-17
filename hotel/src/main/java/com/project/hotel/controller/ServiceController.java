package com.project.hotel.controller;
import com.project.hotel.dto.request.CreateServiceRequest;
import com.project.hotel.dto.request.UpdateServiceRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.ServiceResponse;
import com.project.hotel.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    public ApiResponse<ServiceResponse> createService(@RequestBody CreateServiceRequest request) {
        return ApiResponse.<ServiceResponse>builder()
                .result(serviceService.createService(request))
                .build();
    }

    @GetMapping("/hotel/{hotelId}")
    public ApiResponse<List<ServiceResponse>> getServicesByHotel(@PathVariable Integer hotelId) {
        return ApiResponse.<List<ServiceResponse>>builder()
                .result(serviceService.getServicesByHotel(hotelId))
                .build();
    }

    @GetMapping("/hotel-admin/hotel/{hotelId}")
    public ApiResponse<List<ServiceResponse>> getServicesByHotelForHotelAdmin(@PathVariable Integer hotelId) {
        return ApiResponse.<List<ServiceResponse>>builder()
                .result(serviceService.getServicesByHotelForHotelAdmin(hotelId))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceResponse> updateService(@PathVariable Integer id, @RequestBody UpdateServiceRequest request) {
        return ApiResponse.<ServiceResponse>builder()
                .result(serviceService.updateService(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteService(@PathVariable Integer id) {
        serviceService.deleteService(id);
        return ApiResponse.<String>builder()
                .result("Service deleted successfully")
                .build();
    }
}