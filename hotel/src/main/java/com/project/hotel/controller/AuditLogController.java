package com.project.hotel.controller;

import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.entity.AuditLog;
import com.project.hotel.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<List<AuditLog>> getAllLogs() {
        return ApiResponse.<List<AuditLog>>builder()
                .result(auditLogService.getAllLogs())
                .build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<List<AuditLog>> getLogsByUser(@PathVariable int userId) {
        return ApiResponse.<List<AuditLog>>builder()
                .result(auditLogService.getLogsByUser(userId))
                .build();
    }
}