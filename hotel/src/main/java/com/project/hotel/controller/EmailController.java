package com.project.hotel.controller;

import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailController {
    private final EmailVerificationService verificationService;

    @PostMapping("/send-code")
    public ApiResponse<String> sendCode(@RequestParam String email) {
        verificationService.sendVerificationCode(email);
        return ApiResponse.<String>builder()
                .message("Verification code sent to " + email)
                .build();
    }

    @PostMapping("/verify-code")
    public ApiResponse<String> verifyCode(@RequestParam String email,
                                             @RequestParam String code) {
        boolean success = verificationService.verifyCode(email, code);
        if (success) {
            return ApiResponse.<String>builder()
                    .result("Email verified successfully!")
                    .build();
        }
        return ApiResponse.<String>builder()
                .result("Invalid verification code!")
                .build();
    }
}
