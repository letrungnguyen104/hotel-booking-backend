// src/main/java/com/project/hotel/controller/DashboardController.java
package com.project.hotel.controller;

import com.project.hotel.dto.response.AdminDashboardDataResponse;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {

    DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<AdminDashboardDataResponse> getAdminDashboardData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.<AdminDashboardDataResponse>builder()
                .result(dashboardService.getAdminDashboardData(startDate, endDate))
                .build();
    }
}