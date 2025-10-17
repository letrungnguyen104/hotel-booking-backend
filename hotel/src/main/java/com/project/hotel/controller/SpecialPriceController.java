package com.project.hotel.controller;

import com.project.hotel.dto.request.CreateSpecialPriceRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.SpecialPriceResponse;
import com.project.hotel.service.SpecialPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/special-prices")
@RequiredArgsConstructor
public class SpecialPriceController {
    private final SpecialPriceService specialPriceService;

    @PostMapping
    public ApiResponse<SpecialPriceResponse> create(@RequestBody CreateSpecialPriceRequest request) {
        return ApiResponse.<SpecialPriceResponse>builder()
                .result(specialPriceService.createSpecialPrice(request))
                .build();
    }

    @GetMapping("/room-type/{roomTypeId}")
    public ApiResponse<List<SpecialPriceResponse>> getByRoomType(@PathVariable Integer roomTypeId) {
        return ApiResponse.<List<SpecialPriceResponse>>builder()
                .result(specialPriceService.getSpecialPricesForRoomType(roomTypeId))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Integer id) {
        specialPriceService.deleteSpecialPrice(id);
        return ApiResponse.<String>builder().
                result("Deleted successfully").
                build();
    }
}
