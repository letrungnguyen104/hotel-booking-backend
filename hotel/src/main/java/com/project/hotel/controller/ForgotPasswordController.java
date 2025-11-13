package com.project.hotel.controller;

import com.project.hotel.dto.request.ResetPasswordRequest;
import com.project.hotel.dto.request.SendOtpRequest;
import com.project.hotel.dto.request.VerifyOtpRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.ForgotPasswordResponse;
import com.project.hotel.service.ForgotPasswordService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth/forgot-password")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ForgotPasswordController {
    
    ForgotPasswordService forgotPasswordService;
    
    @PostMapping("/send-otp")
    public ApiResponse<ForgotPasswordResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        log.info("Sending OTP to email: {}", request.getEmail());
        ForgotPasswordResponse response = forgotPasswordService.sendOtp(request);
        return ApiResponse.<ForgotPasswordResponse>builder()
                .result(response)
                .build();
    }
    
    @PostMapping("/verify-otp")
    public ApiResponse<ForgotPasswordResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verifying OTP for email: {}", request.getEmail());
        ForgotPasswordResponse response = forgotPasswordService.verifyOtp(request);
        return ApiResponse.<ForgotPasswordResponse>builder()
                .result(response)
                .build();
    }
    
    @PostMapping("/reset-password")
    public ApiResponse<ForgotPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Resetting password for email: {}", request.getEmail());
        ForgotPasswordResponse response = forgotPasswordService.resetPassword(request);
        return ApiResponse.<ForgotPasswordResponse>builder()
                .result(response)
                .build();
    }
}
