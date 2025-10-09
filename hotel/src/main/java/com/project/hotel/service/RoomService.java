package com.project.hotel.service;

import com.project.hotel.dto.request.CreateRoomRequest;
import com.project.hotel.dto.request.UpdateRoomRequest;
import com.project.hotel.dto.response.RoomResponse;
import com.project.hotel.dto.response.RoomWithTypeResponse;
import com.project.hotel.entity.Room;
import com.project.hotel.entity.RoomType;
import com.project.hotel.enums.RoomStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.RoomMapper;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.RoomTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    RoomRepository roomRepository;
    RoomTypeRepository roomTypeRepository;
    RoomMapper roomMapper;

    public RoomResponse createRoom(CreateRoomRequest request) {
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        if (roomRepository.existsByRoomNumberAndRoomType(request.getRoomNumber(), roomType)) {
            throw new AppException(ErrorCode.ROOM_NUMBER_ALREADY_EXISTS);
        }
        Room room = roomMapper.toRoom(request);
        room.setRoomType(roomType);
        room.setStatus(RoomStatus.AVAILABLE);
        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    public RoomResponse getRoomById(int id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        return roomMapper.toRoomResponse(room);
    }

    public RoomResponse updateRoom(int id, UpdateRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        if (!room.getRoomNumber().equals(request.getRoomNumber())
                && roomRepository.existsByRoomNumberAndRoomType(request.getRoomNumber(), room.getRoomType())) {
            throw new AppException(ErrorCode.ROOM_NUMBER_ALREADY_EXISTS);
        }
        room.setFloor(request.getFloor());
        room.setRoomNumber(request.getRoomNumber());
        room.setStatus(request.getStatus());

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    public void deleteRoom(int id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        room.setStatus(RoomStatus.CLOSED);
    }

    public List<RoomResponse> getRoomsByRoomType(int roomTypeId) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        List<Room> rooms = roomRepository.findAll().stream()
                .filter(r -> r.getRoomType().equals(roomType))
                .collect(Collectors.toList());
        return rooms.stream()
                .map(roomMapper::toRoomResponse)
                .collect(Collectors.toList());
    }

    public List<RoomWithTypeResponse> getRoomsWithTypeByHotel(int hotelId) {
        List<Room> rooms = roomRepository.findAll().stream()
                .filter(r -> r.getRoomType().getHotel().getId() == hotelId)
                .collect(Collectors.toList());

        return rooms.stream()
                .map(roomMapper::toRoomWithTypeResponse)
                .collect(Collectors.toList());
    }
}