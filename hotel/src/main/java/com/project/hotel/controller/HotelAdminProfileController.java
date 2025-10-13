package com.project.hotel.controller;

import com.project.hotel.dto.request.CreateHotelAdminRequest;
import com.project.hotel.dto.request.UpdateHotelAdminRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.HotelAdminResponse;
import com.project.hotel.service.HotelAdminProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hotel-admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HotelAdminProfileController {

    HotelAdminProfileService hotelAdminProfileService;

    @PostMapping
    ApiResponse<HotelAdminResponse> createHotelAdmin(@RequestBody CreateHotelAdminRequest request) {
        return ApiResponse.<HotelAdminResponse>builder()
                .result(hotelAdminProfileService.createHotelAdmin(request))
                .build();
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<HotelAdminResponse> verifyHotelAdmin(@PathVariable int id) {
        return ApiResponse.<HotelAdminResponse>builder()
                .result(hotelAdminProfileService.verifyHotelAdmin(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HOTEL_ADMIN')")
    public ApiResponse<HotelAdminResponse> updateHotelAdmin(
            @PathVariable int id,
            @RequestBody UpdateHotelAdminRequest request) {
        return ApiResponse.<HotelAdminResponse>builder()
                .result(hotelAdminProfileService.updateHotelAdmin(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ApiResponse<Void> deleteHotelAdmin(@PathVariable int id) {
        hotelAdminProfileService.deleteHotelAdmin(id);
        return ApiResponse.<Void>builder()
                .message("Delete Hotel Admin Profile successfully!")
                .build();
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ApiResponse<List<HotelAdminResponse>> getHotelAdminList() {
        return ApiResponse.<List<HotelAdminResponse>>builder()
                .result(hotelAdminProfileService.getListHotelAdmin())
                .build();
    }

    @GetMapping("/my-profile")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<HotelAdminResponse> getMyBusinessProfile() {
        return ApiResponse.<HotelAdminResponse>builder()
                .result(hotelAdminProfileService.getMyBusinessProfile())
                .build();
    }

    @GetMapping("/my-status")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<HotelAdminResponse> checkMyBusinessProfileStatus() {
        Optional<HotelAdminResponse> profileOpt = hotelAdminProfileService.checkMyBusinessProfile();
        return ApiResponse.<HotelAdminResponse>builder()
                .result(profileOpt.orElse(null))
                .build();
    }

}
