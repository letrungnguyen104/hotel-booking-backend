package com.project.hotel.controller;

import com.project.hotel.dto.request.GuestInquiryRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.entity.GuestInquiry;
import com.project.hotel.service.GuestInquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
public class GuestInquiryController {

    private final GuestInquiryService guestInquiryService;

    @PostMapping("/public")
    public ApiResponse<String> submitInquiry(@Valid @RequestBody GuestInquiryRequest request) {
        guestInquiryService.createInquiry(request);
        return ApiResponse.<String>builder()
                .result("Your message has been sent successfully. We will get back to you soon.")
                .build();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<List<GuestInquiry>> getAllInquiries() {
        return ApiResponse.<List<GuestInquiry>>builder()
                .result(guestInquiryService.getAllInquiries())
                .build();
    }

    @PostMapping("/admin/{inquiryId}/reply")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<GuestInquiry> replyToInquiry(
            @PathVariable Integer inquiryId,
            @RequestBody Map<String, String> requestBody) {

        return ApiResponse.<GuestInquiry>builder()
                .result(guestInquiryService.replyToInquiry(inquiryId, requestBody))
                .build();
    }
}