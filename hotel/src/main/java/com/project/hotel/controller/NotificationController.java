package com.project.hotel.controller;

import com.project.hotel.dto.request.CreateNotificationRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.NotificationResponse;
import com.project.hotel.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/admin/send")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<NotificationResponse> createNotification(@RequestBody @Valid CreateNotificationRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.createNotification(request))
                .build();
    }

    @GetMapping("/my-notifications")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<NotificationResponse>> getMyNotifications() {
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notificationService.getMyNotifications())
                .build();
    }

    @PatchMapping("/read/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<NotificationResponse> markAsRead(@PathVariable Integer id) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.markAsRead(id))
                .build();
    }

    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.<String>builder()
                .result("All marked as read")
                .build();
    }
}