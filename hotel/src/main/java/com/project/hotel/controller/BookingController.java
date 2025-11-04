// src/main/java/com/project/hotel/controller/BookingController.java
package com.project.hotel.controller;

import com.project.hotel.dto.request.BookingRequest;
import com.project.hotel.dto.request.CancelBookingRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.BookingDetailResponse;
import com.project.hotel.dto.response.CreatePaymentResponse;
import com.project.hotel.dto.response.DashboardDataResponse;
import com.project.hotel.service.BookingProcessingService;
import com.project.hotel.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    BookingService bookingService;
    BookingProcessingService bookingProcessingService;

    @PostMapping("/create-payment")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CreatePaymentResponse> createBookingAndPayment(
            @RequestBody BookingRequest request,
            HttpServletRequest httpServletRequest) {
        return ApiResponse.<CreatePaymentResponse>builder()
                .result(bookingService.createBookingAndPayment(request, httpServletRequest))
                .build();
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Void> vnpayReturn(
            @RequestParam Map<String, String> allParams
    ) throws IOException {
        log.info("VNPAY Return call received with params: {}", allParams);
        bookingProcessingService.verifySignature(allParams);

        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
        String bookingId = allParams.get("vnp_TxnRef");
        String transactionNo = allParams.get("vnp_TransactionNo");

        String frontendRedirectUrl;

        if ("00".equals(vnp_ResponseCode)) {
            bookingProcessingService.confirmPayment(bookingId, transactionNo);
            frontendRedirectUrl = "http://localhost:5173/booking-success";
        } else {
            bookingProcessingService.failPayment(bookingId, transactionNo);
            frontendRedirectUrl = "http://localhost:5173/booking-failure";
        }

        return ResponseEntity.status(302).header("Location", frontendRedirectUrl).build();
    }

    @GetMapping("/hotel-admin/dashboard")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<List<DashboardDataResponse>> getDashboardData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.<List<DashboardDataResponse>>builder()
                .result(bookingService.getDashboardData(startDate, endDate))
                .build();
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<BookingDetailResponse>> getMyBookings() {
        return ApiResponse.<List<BookingDetailResponse>>builder()
                .result(bookingService.getMyBookings())
                .build();
    }

    @PatchMapping("/cancel/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> cancelBooking(@PathVariable Integer bookingId,
                                             @RequestBody @Valid CancelBookingRequest request) {
        bookingService.cancelBooking(bookingId, request);
        return ApiResponse.<String>builder().result("Booking cancelled successfully").build();
    }

    @GetMapping("/hotel-admin")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<List<BookingDetailResponse>> getBookingsForHotelAdmin(@RequestParam(required = false) String status) {
        return ApiResponse.<List<BookingDetailResponse>>builder()
                .result(bookingService.getBookingsForHotelAdmin(status))
                .build();
    }

    @PatchMapping("/hotel-admin/confirm/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<BookingDetailResponse> confirmBooking(@PathVariable Integer bookingId) {
        return ApiResponse.<BookingDetailResponse>builder()
                .result(bookingService.confirmBooking(bookingId))
                .build();
    }

    @PatchMapping("/hotel-admin/check-in/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<BookingDetailResponse> checkInBooking(@PathVariable Integer bookingId) {
        return ApiResponse.<BookingDetailResponse>builder()
                .result(bookingService.checkInBooking(bookingId))
                .build();
    }

    @PatchMapping("/hotel-admin/check-out/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<BookingDetailResponse> checkOutBooking(@PathVariable Integer bookingId) {
        return ApiResponse.<BookingDetailResponse>builder()
                .result(bookingService.checkOutBooking(bookingId))
                .build();
    }

    @PatchMapping("/hotel-admin/approve-cancellation/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<BookingDetailResponse> approveCancellation(@PathVariable Integer bookingId) {
        return ApiResponse.<BookingDetailResponse>builder()
                .result(bookingService.approveCancellation(bookingId))
                .build();
    }

    @PatchMapping("/hotel-admin/reject-cancellation/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public ApiResponse<BookingDetailResponse> rejectCancellation(@PathVariable Integer bookingId) {
        return ApiResponse.<BookingDetailResponse>builder()
                .result(bookingService.rejectCancellation(bookingId))
                .build();
    }
}