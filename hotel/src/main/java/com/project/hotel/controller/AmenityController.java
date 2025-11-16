package com.project.hotel.controller;

import com.project.hotel.dto.request.CreateAmenityRequest;
import com.project.hotel.dto.response.AmenityResponse;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.service.AmenityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/amenity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AmenityController {

    AmenityService amenityService;

    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_ADMIN')")
    @PostMapping
    public ApiResponse<AmenityResponse> createAmenity(@RequestBody CreateAmenityRequest request) {
        return ApiResponse.<AmenityResponse>builder()
                .result(amenityService.createAmenity(request))
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_ADMIN')")
    @GetMapping
    public ApiResponse<List<AmenityResponse>> getAllAmenities() {
        return ApiResponse.<List<AmenityResponse>>builder()
                .result(amenityService.getAllAmenities())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AmenityResponse> getAmenityById(@PathVariable int id) {
        return ApiResponse.<AmenityResponse>builder()
                .result(amenityService.getAmenityById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AmenityResponse> updateAmenity(
            @PathVariable int id,
            @RequestBody CreateAmenityRequest request
    ) {
        return ApiResponse.<AmenityResponse>builder()
                .result(amenityService.updateAmenity(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAmenity(@PathVariable int id) {
        amenityService.deleteAmenity(id);
        return ApiResponse.<Void>builder()
                .message("Amenity deleted successfully")
                .build();
    }
}