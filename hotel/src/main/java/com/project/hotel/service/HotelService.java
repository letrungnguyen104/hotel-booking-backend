package com.project.hotel.service;

import com.project.hotel.dto.request.CreateHotelRequest;
import com.project.hotel.dto.request.UpdateHotelRequest;
import com.project.hotel.dto.response.HotelResponse;
import com.project.hotel.dto.response.HotelSearchResponse;
import com.project.hotel.dto.response.OwnerResponse;
import com.project.hotel.entity.Hotel;
import com.project.hotel.entity.HotelImage;
import com.project.hotel.entity.User;
import com.project.hotel.enums.HotelStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.HotelImageRepository;
import com.project.hotel.repository.HotelRepository;
import com.project.hotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HotelService {
    HotelRepository hotelRepository;
    HotelImageRepository hotelImageRepository;
    UserRepository userRepository;
    FileStorageService fileStorageService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HOTEL_ADMIN')")
    public HotelResponse createHotel(CreateHotelRequest request, List<MultipartFile> files) {
        System.out.println(request.getOwnerId());
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Hotel hotel = Hotel.builder()
                .owner(owner)
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .phone(request.getPhone())
                .description(request.getDescription())
                .status(HotelStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        hotelRepository.save(hotel);

        List<String> imageUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String url = fileStorageService.saveFile(file);

                HotelImage image = HotelImage.builder()
                        .hotel(hotel)
                        .url(url)
                        .isMain(i == 0)
                        .createdAt(LocalDateTime.now())
                        .build();

                hotelImageRepository.save(image);
                imageUrls.add(url);
            }
        }

        return mapToResponse(hotel, imageUrls);
    }

    public HotelResponse getHotel(int id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        List<String> imageUrls = hotelImageRepository.findByHotel(hotel)
                .stream()
                .map(HotelImage::getUrl)
                .toList();

        return mapToResponse(hotel, imageUrls);
    }

    public List<HotelResponse> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll()
                .stream()
                .filter(h -> h.getStatus() != HotelStatus.CLOSED)
                .toList();

        List<HotelResponse> responses = new ArrayList<>();
        for (Hotel hotel : hotels) {
            List<String> imageUrls = hotelImageRepository.findByHotel(hotel)
                    .stream()
                    .map(HotelImage::getUrl)
                    .toList();
            responses.add(mapToResponse(hotel, imageUrls));
        }
        return responses;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HOTEL_ADMIN')")
    public HotelResponse updateHotel(int id, UpdateHotelRequest request, List<MultipartFile> files) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());
        hotel.setCity(request.getCity());
        hotel.setCountry(request.getCountry());
        hotel.setPhone(request.getPhone());
        hotel.setDescription(request.getDescription());
        hotel.setStatus(request.getStatus());
        hotelRepository.save(hotel);

        List<String> imageUrls = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            // Xóa ảnh cũ an toàn
            hotelImageRepository.deleteAll(hotelImageRepository.findByHotel(hotel));

            // Thêm ảnh mới
            List<HotelImage> newImages = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String url = fileStorageService.saveFile(file);

                HotelImage image = HotelImage.builder()
                        .hotel(hotel)
                        .url(url)
                        .isMain(i == 0)
                        .createdAt(LocalDateTime.now())
                        .build();

                newImages.add(image);
                imageUrls.add(url);
            }
            hotelImageRepository.saveAll(newImages);
        } else {
            imageUrls = hotelImageRepository.findByHotel(hotel)
                    .stream()
                    .map(HotelImage::getUrl)
                    .toList();
        }

        return mapToResponse(hotel, imageUrls);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HOTEL_ADMIN')")
    public HotelResponse patchHotel(int id, UpdateHotelRequest request, List<MultipartFile> files) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        if (request != null) {
            if (request.getName() != null) hotel.setName(request.getName());
            if (request.getAddress() != null) hotel.setAddress(request.getAddress());
            if (request.getCity() != null) hotel.setCity(request.getCity());
            if (request.getCountry() != null) hotel.setCountry(request.getCountry());
            if (request.getPhone() != null) hotel.setPhone(request.getPhone());
            if (request.getDescription() != null) hotel.setDescription(request.getDescription());
            if (request.getStatus() != null) hotel.setStatus(request.getStatus());
        }

        List<String> imageUrls = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            hotelImageRepository.deleteAll(hotelImageRepository.findByHotel(hotel));
            List<HotelImage> newImages = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String url = fileStorageService.saveFile(file);

                HotelImage image = HotelImage.builder()
                        .hotel(hotel)
                        .url(url)
                        .isMain(i == 0)
                        .createdAt(LocalDateTime.now())
                        .build();

                newImages.add(image);
                imageUrls.add(url);
            }
            hotelImageRepository.saveAll(newImages);
        } else {
            imageUrls = hotelImageRepository.findByHotel(hotel)
                    .stream()
                    .map(HotelImage::getUrl)
                    .toList();
        }

        hotelRepository.save(hotel);
        return mapToResponse(hotel, imageUrls);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HOTEL_ADMIN')")
    public void deleteHotel(int id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        hotel.setStatus(HotelStatus.CLOSED);
        hotelRepository.save(hotel);
    }

    private HotelResponse mapToResponse(Hotel hotel, List<String> imageUrls) {
        return HotelResponse.builder()
                .id(hotel.getId())
                .owner(OwnerResponse.builder()
                        .id(hotel.getOwner().getId())
                        .username(hotel.getOwner().getUsername())
                        .email(hotel.getOwner().getEmail())
                        .build())
                .name(hotel.getName())
                .address(hotel.getAddress())
                .city(hotel.getCity())
                .country(hotel.getCountry())
                .phone(hotel.getPhone())
                .description(hotel.getDescription())
                .status(String.valueOf(hotel.getStatus()))
                .createdAt(hotel.getCreatedAt())
                .images(imageUrls)
                .build();
    }

    public List<HotelSearchResponse> searchHotels(String city, int guests, LocalDate checkIn, LocalDate checkOut) {
        List<Object[]> results = hotelRepository.searchHotels(city, guests, checkIn, checkOut);
        return results.stream().map(row -> HotelSearchResponse.builder()
                .id((Integer) row[0])
                .name((String) row[1])
                .city((String) row[2])
                .country((String) row[3])
                .amenities((String) row[4])
                .oldPrice(row[5] != null ? ((Number) row[5]).doubleValue() : null)
                .newPrice(row[6] != null ? ((Number) row[6]).doubleValue() : null)
                .stars(row[7] != null ? ((Number) row[7]).doubleValue() : 0.0)
                .reviewCount(row[8] != null ? ((Number) row[8]).longValue() : 0L)
                .build()
        ).collect(Collectors.toList());
    }
}