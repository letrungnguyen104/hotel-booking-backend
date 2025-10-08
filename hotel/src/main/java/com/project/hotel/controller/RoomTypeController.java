package com.project.hotel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotel.dto.request.CreateRoomTypeRequest;
import com.project.hotel.dto.request.UpdateRoomTypeRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.RoomTypeResponse;
import com.project.hotel.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/room-type")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RoomTypeResponse> createRoomType(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws JsonProcessingException {
        CreateRoomTypeRequest request = new ObjectMapper().readValue(requestJson, CreateRoomTypeRequest.class);
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.createRoomType(request, files))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoomTypeResponse>> getAllRoomTypes() {
        return ApiResponse.<List<RoomTypeResponse>>builder()
                .result(roomTypeService.getAllRoomTypes())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomTypeResponse> getRoomTypeById(@PathVariable int id) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.getRoomTypeById(id))
                .build();
    }

    @GetMapping("/hotel/{hotelId}")
    public ApiResponse<List<RoomTypeResponse>> getRoomTypesByHotelId(@PathVariable int hotelId) {
        return ApiResponse.<List<RoomTypeResponse>>builder()
                .result(roomTypeService.getRoomTypesByHotelId(hotelId))
                .build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RoomTypeResponse> updateRoomType(
            @PathVariable int id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "remainingImages", required = false) String remainingImagesJson
    ) throws JsonProcessingException {
        UpdateRoomTypeRequest request = new ObjectMapper().readValue(requestJson, UpdateRoomTypeRequest.class);
        List<String> remainingImages = remainingImagesJson != null
                ? new ObjectMapper().readValue(remainingImagesJson, List.class)
                : null;

        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.updateRoomType(id, request, files, remainingImages))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRoomType(@PathVariable int id) {
        roomTypeService.deleteRoomType(id);
        return ApiResponse.<String>builder()
                .result("Room type has been closed successfully.")
                .build();
    }
}