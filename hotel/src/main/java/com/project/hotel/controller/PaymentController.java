package com.project.hotel.controller;

import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.PaymentHistoryResponse;
import com.project.hotel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/hotel-admin/history")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<List<PaymentHistoryResponse>> getHotelPaymentHistory() {
        return ApiResponse.<List<PaymentHistoryResponse>>builder()
                .result(paymentService.getPaymentHistoryForOwner())
                .build();
    }
}