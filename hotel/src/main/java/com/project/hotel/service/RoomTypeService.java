package com.project.hotel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotel.dto.request.CreateRoomTypeRequest;
import com.project.hotel.dto.request.UpdateRoomTypeRequest;
import com.project.hotel.dto.response.AmenityResponse;
import com.project.hotel.dto.response.RoomTypeResponse;
import com.project.hotel.entity.*;
import com.project.hotel.enums.RoomTypeStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomTypeService {

    RoomTypeRepository roomTypeRepository;
    HotelRepository hotelRepository;
    AmenityRepository amenityRepository;
    RoomTypeImageRepository roomTypeImageRepository;
    FileStorageService fileStorageService;

    @Transactional
    public RoomTypeResponse createRoomType(CreateRoomTypeRequest request, List<MultipartFile> files) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        List<Amenity> amenities = amenityRepository.findAllById(request.getAmenityIds());
        RoomType roomType = RoomType.builder()
                .hotel(hotel)
                .name(request.getName())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .pricePerNight(request.getPricePerNight())
                .status(RoomTypeStatus.ACTIVE)
                .amenities(amenities)
                .build();

        roomTypeRepository.save(roomType);
        List<String> imageUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String url = fileStorageService.saveFile(file);

                RoomTypeImage image = RoomTypeImage.builder()
                        .roomType(roomType)
                        .url(url)
                        .isMain(i == 0)
                        .createdAt(LocalDateTime.now())
                        .build();

                roomTypeImageRepository.save(image);
                imageUrls.add(url);
            }
        }
        return RoomTypeResponse.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .description(roomType.getDescription())
                .capacity(roomType.getCapacity())
                .pricePerNight(roomType.getPricePerNight())
                .status(String.valueOf(roomType.getStatus()))
                .images(imageUrls)
                .amenities(amenities.stream()
                        .map(a -> AmenityResponse.builder()
                                .id(a.getId())
                                .name(a.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getAllRoomTypes() {
        List<RoomType> roomTypes = roomTypeRepository.findAllWithImages();
        roomTypes.forEach(r -> r.getAmenities().size());

        return roomTypes.stream()
                .map(r -> RoomTypeResponse.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .description(r.getDescription())
                        .capacity(r.getCapacity())
                        .pricePerNight(r.getPricePerNight())
                        .status(String.valueOf(r.getStatus()))
                        .images(r.getImages().stream().map(RoomTypeImage::getUrl).toList())
                        .amenities(r.getAmenities().stream()
                                .map(a -> AmenityResponse.builder()
                                        .id(a.getId())
                                        .name(a.getName())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @Transactional
    public RoomTypeResponse getRoomTypeById(int id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        return RoomTypeResponse.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .description(roomType.getDescription())
                .capacity(roomType.getCapacity())
                .pricePerNight(roomType.getPricePerNight())
                .status(String.valueOf(roomType.getStatus()))
                .images(roomType.getImages().stream()
                        .map(RoomTypeImage::getUrl)
                        .toList())
                .amenities(roomType.getAmenities().stream()
                        .map(a -> AmenityResponse.builder()
                                .id(a.getId())
                                .name(a.getName())
                                .build())
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getRoomTypesByHotelId(int hotelId) {
        List<RoomType> roomTypes = roomTypeRepository.findByHotelIdWithImages(hotelId);
        roomTypes.forEach(r -> r.getAmenities().size());

        return roomTypes.stream()
                .map(r -> RoomTypeResponse.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .description(r.getDescription())
                        .capacity(r.getCapacity())
                        .pricePerNight(r.getPricePerNight())
                        .status(String.valueOf(r.getStatus()))
                        .images(r.getImages().stream().map(RoomTypeImage::getUrl).toList())
                        .amenities(r.getAmenities().stream()
                                .map(a -> AmenityResponse.builder()
                                        .id(a.getId())
                                        .name(a.getName())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @Transactional
    public RoomTypeResponse updateRoomType(
            int id,
            UpdateRoomTypeRequest request,
            List<MultipartFile> files,
            List<String> remainingImages
    ) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setCapacity(request.getCapacity());
        roomType.setPricePerNight(request.getPricePerNight());

        if (request.getStatus() != null) {
            try {
                roomType.setStatus(RoomTypeStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_STATUS);
            }
        }

        List<Amenity> amenitiesList = amenityRepository.findAllById(request.getAmenityIds());
        roomType.setAmenities(amenitiesList);

        List<RoomTypeImage> currentImages = roomTypeImageRepository.findByRoomType(roomType);
        final List<String> effectiveRemaining =
                (remainingImages == null)
                        ? currentImages.stream().map(RoomTypeImage::getUrl).toList()
                        : remainingImages;
        List<RoomTypeImage> toDelete = currentImages.stream()
                .filter(img -> !effectiveRemaining.contains(img.getUrl()))
                .toList();
        if (!toDelete.isEmpty()) {
            roomTypeImageRepository.deleteAll(toDelete);
            toDelete.forEach(img -> fileStorageService.deleteFile(img.getUrl()));
        }
        if (files != null && !files.isEmpty()) {
            List<RoomTypeImage> newImages = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = fileStorageService.saveFile(file);
                newImages.add(RoomTypeImage.builder()
                        .roomType(roomType)
                        .url(url)
                        .isMain(false)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
            roomTypeImageRepository.saveAll(newImages);
        }

        List<String> imageUrls = roomTypeImageRepository.findByRoomType(roomType)
                .stream()
                .map(RoomTypeImage::getUrl)
                .toList();

        roomTypeRepository.save(roomType);

        return RoomTypeResponse.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .description(roomType.getDescription())
                .capacity(roomType.getCapacity())
                .pricePerNight(roomType.getPricePerNight())
                .status(String.valueOf(roomType.getStatus()))
                .images(imageUrls)
                .amenities(roomType.getAmenities().stream()
                        .map(a -> AmenityResponse.builder()
                                .id(a.getId())
                                .name(a.getName())
                                .build())
                        .toList())
                .build();
    }

    @Transactional
    public void deleteRoomType(int id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        roomType.setStatus(RoomTypeStatus.CLOSED);
        roomTypeRepository.save(roomType);
    }
}
