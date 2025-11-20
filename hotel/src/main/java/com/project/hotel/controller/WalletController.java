package com.project.hotel.controller;

import com.project.hotel.dto.request.WithdrawRequestDto;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.entity.User;
import com.project.hotel.entity.Wallet;
import com.project.hotel.entity.WithdrawalRequest;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.service.WalletService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletController {

    WalletService walletService;
    UserRepository userRepository;

    @GetMapping("/my-wallet")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<Wallet> getMyWallet() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return ApiResponse.<Wallet>builder()
                .result(walletService.getMyWallet(user.getId()))
                .build();
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<List<WithdrawalRequest>> getMyWithdrawalHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return ApiResponse.<List<WithdrawalRequest>>builder()
                .result(walletService.getWithdrawalHistory(user.getId()))
                .build();
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<String> requestWithdrawal(@RequestBody @Valid WithdrawRequestDto request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        walletService.requestWithdrawal(user.getId(), request);
        return ApiResponse.<String>builder().result("Request submitted").build();
    }

    @GetMapping("/admin/requests")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<List<WithdrawalRequest>> getPendingRequests() {
        return ApiResponse.<List<WithdrawalRequest>>builder().result(walletService.getPendingRequests()).build();
    }

    @PatchMapping("/admin/requests/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> approveRequest(@PathVariable Integer id) {
        walletService.approveWithdrawal(id);
        return ApiResponse.<String>builder().result("Approved").build();
    }

    @PatchMapping("/admin/requests/{id}/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> rejectRequest(@PathVariable Integer id) {
        walletService.rejectWithdrawal(id);
        return ApiResponse.<String>builder().result("Rejected").build();
    }
}