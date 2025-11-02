package com.project.hotel.controller;

import com.project.hotel.dto.request.CreateReportRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.ReportResponse;
import com.project.hotel.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> createReport(@Valid @RequestBody CreateReportRequest request) {
        reportService.createReport(request);
        return ApiResponse.<String>builder()
                .result("Report submitted successfully. Admin will review it soon.")
                .build();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<List<ReportResponse>> getAllReports(@RequestParam(required = false) String status) {
        return ApiResponse.<List<ReportResponse>>builder()
                .result(reportService.getAllReports(status))
                .build();
    }

    @PatchMapping("/admin/{reportId}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<ReportResponse> updateReportStatus(
            @PathVariable Integer reportId,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");
        if (status == null) throw new IllegalArgumentException("Status is required");

        return ApiResponse.<ReportResponse>builder()
                .result(reportService.updateReportStatus(reportId, status))
                .build();
    }
}