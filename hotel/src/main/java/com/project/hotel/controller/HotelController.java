package com.project.hotel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotel.dto.request.CreateHotelRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.HotelResponse;
import com.project.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<HotelResponse> createHotel(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws JsonProcessingException {
        CreateHotelRequest request = new ObjectMapper().readValue(requestJson, CreateHotelRequest.class);
        return ApiResponse.<HotelResponse>builder()
                .result(hotelService.createHotel(request, files))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<HotelResponse> getHotel(@PathVariable int id) {
        return ApiResponse.<HotelResponse>builder()
                .result(hotelService.getHotel(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<HotelResponse>> getAllHotels() {
        return ApiResponse.<List<HotelResponse>>builder()
                .result(hotelService.getAllHotels())
                .build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<HotelResponse> updateHotel(
            @PathVariable int id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws JsonProcessingException {
        CreateHotelRequest request = new ObjectMapper().readValue(requestJson, CreateHotelRequest.class);
        return ApiResponse.<HotelResponse>builder()
                .result(hotelService.updateHotel(id, request, files))
                .build();
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<HotelResponse> patchHotel(
            @PathVariable int id,
            @RequestPart(value = "request", required = false) String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws JsonProcessingException {
        CreateHotelRequest request = null;
        if (requestJson != null) {
            request = new ObjectMapper().readValue(requestJson, CreateHotelRequest.class);
        }
        return ApiResponse.<HotelResponse>builder()
                .result(hotelService.patchHotel(id, request, files))
                .build();
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteHotel(@PathVariable int id) {
        hotelService.deleteHotel(id);
        return ApiResponse.<Void>builder()
                .message("Delete Successfully!")
                .build();
    }
}
