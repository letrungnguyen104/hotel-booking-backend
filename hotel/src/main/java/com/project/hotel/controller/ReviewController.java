package com.project.hotel.controller;

import com.project.hotel.dto.request.CreateReviewRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.ReviewResponse;
import com.project.hotel.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.createReview(request))
                .build();
    }

    @GetMapping("/hotel/{hotelId}")
    public ApiResponse<List<ReviewResponse>> getReviewsForHotel(@PathVariable Integer hotelId) {
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.getReviewsForHotel(hotelId))
                .build();
    }
}