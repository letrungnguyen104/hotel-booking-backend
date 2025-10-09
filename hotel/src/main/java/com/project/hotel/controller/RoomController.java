package com.project.hotel.controller;

import com.project.hotel.dto.request.CreateRoomRequest;
import com.project.hotel.dto.request.UpdateRoomRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.RoomResponse;
import com.project.hotel.dto.response.RoomWithTypeResponse;
import com.project.hotel.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    RoomService roomService;

    @PostMapping
    ApiResponse<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.createRoom(request))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<RoomResponse> getRoom(@PathVariable int id) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.getRoomById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<RoomResponse> updateRoom(@PathVariable int id, @RequestBody UpdateRoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoom(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteRoom(@PathVariable int id) {
        roomService.deleteRoom(id);
        return ApiResponse.<String>builder()
                .result("Room deleted successfully")
                .build();
    }

    @GetMapping("/by-room-type/{roomTypeId}")
    ApiResponse<List<RoomResponse>> getRoomsByRoomType(@PathVariable int roomTypeId) {
        return ApiResponse.<List<RoomResponse>>builder()
                .result(roomService.getRoomsByRoomType(roomTypeId))
                .build();
    }

    @GetMapping("/hotel/{hotelId}/rooms")
    ApiResponse<List<RoomWithTypeResponse>> getRoomsWithTypeByHotel(@PathVariable int hotelId) {
        return ApiResponse.<List<RoomWithTypeResponse>>builder()
                .result(roomService.getRoomsWithTypeByHotel(hotelId))
                .build();
    }
}
