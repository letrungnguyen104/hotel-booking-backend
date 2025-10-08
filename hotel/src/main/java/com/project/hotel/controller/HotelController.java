package com.project.hotel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotel.dto.request.CreateHotelRequest;
import com.project.hotel.dto.request.UpdateHotelRequest;
import com.project.hotel.dto.response.*;
import com.project.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @GetMapping("/get-by-id/{id}")
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

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<HotelResponse> updateHotel(
            @PathVariable int id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "remainingImages", required = false) String remainingImagesJson
    ) throws JsonProcessingException {

        UpdateHotelRequest request = new ObjectMapper().readValue(requestJson, UpdateHotelRequest.class);

        List<String> remainingImages = new ArrayList<>();
        if (remainingImagesJson != null && !remainingImagesJson.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            remainingImages = mapper.readValue(
                    remainingImagesJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
        }

        return ApiResponse.<HotelResponse>builder()
                .result(hotelService.updateHotel(id, request, files, remainingImages))
                .build();
    }

    @PatchMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<HotelResponse> patchHotel(
            @PathVariable int id,
            @RequestPart(value = "request", required = false) String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws JsonProcessingException {
        UpdateHotelRequest request = null;
        if (requestJson != null) {
            request = new ObjectMapper().readValue(requestJson, UpdateHotelRequest.class);
        }
        return ApiResponse.<HotelResponse>builder()
                .result(hotelService.patchHotel(id, request, files))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteHotel(@PathVariable int id) {
        hotelService.deleteHotel(id);
        return ApiResponse.<Void>builder()
                .message("Delete Successfully!")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<HotelSearchResponse>> searchHotels(
            @RequestParam String city,
            @RequestParam int guests,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        return ApiResponse.<List<HotelSearchResponse>>builder()
                .result(hotelService.searchHotels(city, guests, checkIn, checkOut))
                .build();
    }

    @GetMapping("/top-hotels")
    public ApiResponse<List<HotelTopResponse>> getTopHotels(@RequestParam String city) {
        return ApiResponse.<List<HotelTopResponse>>builder()
                .result(hotelService.getTopHotelsByCity(city))
                .build();
    }

    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<HotelResponse>> getHotelsByOwner(
            @PathVariable int ownerId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.<List<HotelResponse>>builder()
                .result(hotelService.getHotelsByOwner(ownerId, status))
                .build();
    }

}
