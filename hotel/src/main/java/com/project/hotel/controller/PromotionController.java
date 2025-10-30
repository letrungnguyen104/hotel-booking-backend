package com.project.hotel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotel.dto.request.PromotionRequest;
import com.project.hotel.dto.request.ValidatePromotionRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.PromotionResponse;
import com.project.hotel.dto.response.ValidatePromotionResponse;
import com.project.hotel.service.PromotionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionController {

    PromotionService promotionService;
    ObjectMapper objectMapper;

    @GetMapping
    public ApiResponse<List<PromotionResponse>> getAllActivePromotions() {
        return ApiResponse.<List<PromotionResponse>>builder()
                .result(promotionService.getActivePromotions())
                .build();
    }

    @GetMapping("/featured")
    public ApiResponse<List<PromotionResponse>> getFeaturedPromotions() {
        return ApiResponse.<List<PromotionResponse>>builder()
                .result(promotionService.getFeaturedPromotions())
                .build();
    }

    @PostMapping("/validate")
    public ApiResponse<ValidatePromotionResponse> validatePromotion(@RequestBody ValidatePromotionRequest request) {
        return ApiResponse.<ValidatePromotionResponse>builder()
                .result(promotionService.validatePromotion(request))
                .build();
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<List<PromotionResponse>> getAllPromotionsForAdmin() {
        return ApiResponse.<List<PromotionResponse>>builder()
                .result(promotionService.getAllPromotionsForAdmin())
                .build();
    }

    @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<PromotionResponse> createPromotion(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {

        PromotionRequest request = objectMapper.readValue(requestJson, PromotionRequest.class);
        return ApiResponse.<PromotionResponse>builder()
                .result(promotionService.createPromotion(request, file))
                .build();
    }

    @PutMapping(value = "/admin/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<PromotionResponse> updatePromotion(
            @PathVariable Integer id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {

        PromotionRequest request = objectMapper.readValue(requestJson, PromotionRequest.class);
        return ApiResponse.<PromotionResponse>builder()
                .result(promotionService.updatePromotion(id, request, file))
                .build();
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> deletePromotion(@PathVariable Integer id) {
        promotionService.deletePromotion(id);
        return ApiResponse.<String>builder().result("Promotion deactivated").build();
    }
}