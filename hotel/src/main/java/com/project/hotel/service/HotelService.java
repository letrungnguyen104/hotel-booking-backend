package com.project.hotel.service;

import com.project.hotel.dto.request.CreateHotelRequest;
import com.project.hotel.dto.request.UpdateHotelRequest;
import com.project.hotel.dto.response.HotelResponse;
import com.project.hotel.dto.response.HotelSearchResponse;
import com.project.hotel.dto.response.HotelTopResponse;
import com.project.hotel.dto.response.OwnerResponse;
import com.project.hotel.entity.Hotel;
import com.project.hotel.entity.HotelImage;
import com.project.hotel.entity.User;
import com.project.hotel.enums.HotelStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.HotelImageRepository;
import com.project.hotel.repository.HotelRepository;
import com.project.hotel.repository.ReviewRepository;
import com.project.hotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    ReviewRepository reviewRepository;
    NotificationService notificationService;

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

        Double rating = reviewRepository.calculateAverageRatingByHotelId(id);
        Long reviewCount = reviewRepository.countByHotelId(id);

        if (rating == null) rating = 0.0;
        if (reviewCount == null) reviewCount = 0L;

        return mapToResponseWithRating(hotel, imageUrls, rating, reviewCount);
    }

    public List<HotelResponse> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll()
                .stream()
                .filter(h -> (h.getStatus() != HotelStatus.CLOSED))
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
    public HotelResponse updateHotel(
            int id,
            UpdateHotelRequest request,
            List<MultipartFile> files,
            List<String> remainingImages
    ) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            hotel.setStatus(request.getStatus());
        } else {
            if (hotel.getOwner().getId() != user.getId()) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
            HotelStatus currentStatus = hotel.getStatus();
            HotelStatus requestedStatus = request.getStatus();

            if (currentStatus != requestedStatus) {
                boolean isAllowedToggle = (currentStatus == HotelStatus.ACTIVE && requestedStatus == HotelStatus.CLOSED) ||
                        (currentStatus == HotelStatus.CLOSED && requestedStatus == HotelStatus.ACTIVE);

                if (!isAllowedToggle) {
                    throw new AppException(ErrorCode.FORBIDDEN_STATUS_CHANGE);
                }
                hotel.setStatus(requestedStatus);
            }
        }

        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());
        hotel.setCity(request.getCity());
        hotel.setCountry(request.getCountry());
        hotel.setPhone(request.getPhone());
        hotel.setDescription(request.getDescription());
        hotel.setStatus(request.getStatus());
        hotelRepository.save(hotel);

        List<HotelImage> currentImages = hotelImageRepository.findByHotel(hotel);
        final List<String> effectiveRemainingImages =
                (remainingImages == null)
                        ? currentImages.stream().map(HotelImage::getUrl).toList()
                        : remainingImages;
        if (effectiveRemainingImages.isEmpty() && (files == null || files.isEmpty())) {
            currentImages.forEach(img -> fileStorageService.deleteFile(img.getUrl()));
            hotelImageRepository.deleteAll(currentImages);
        } else {
            List<HotelImage> toDelete = currentImages.stream()
                    .filter(img -> !effectiveRemainingImages.contains(img.getUrl()))
                    .toList();

            if (!toDelete.isEmpty()) {
                hotelImageRepository.deleteAll(toDelete);
                toDelete.forEach(img -> fileStorageService.deleteFile(img.getUrl()));
            }
        }
        if (files != null && !files.isEmpty()) {
            List<HotelImage> newImages = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = fileStorageService.saveFile(file);
                HotelImage image = HotelImage.builder()
                        .hotel(hotel)
                        .url(url)
                        .isMain(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                newImages.add(image);
            }
            hotelImageRepository.saveAll(newImages);
        }
        List<String> imageUrls = hotelImageRepository.findByHotel(hotel)
                .stream()
                .map(HotelImage::getUrl)
                .toList();

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

    public List<HotelSearchResponse> searchHotels(String address, int guests, LocalDate checkIn, LocalDate checkOut) {
        List<Object[]> results = hotelRepository.searchHotels(address, guests, checkIn, checkOut);
        return results.stream().map(row -> HotelSearchResponse.builder()
                .id((Integer) row[0])
                .address((String) row[1])
                .name((String) row[2])
                .city((String) row[3])
                .country((String) row[4])
                .amenities((String) row[5])
                .oldPrice(row[6] != null ? ((Number) row[6]).doubleValue() : null)
                .newPrice(row[7] != null ? ((Number) row[7]).doubleValue() : null)
                .stars(row[8] != null ? ((Number) row[8]).doubleValue() : 0.0)
                .reviewCount(row[9] != null ? ((Number) row[9]).longValue() : 0L)
                .image((String) row[10])
                .build()
        ).collect(Collectors.toList());
    }

    public List<HotelTopResponse> getTopHotelsByCity(String city) {
        List<Object[]> results = hotelRepository.findTop5HotelsByCity(city);

        return results.stream().map(row -> {
            Double rating = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;

            return HotelTopResponse.builder()
                    .id((Integer) row[0])
                    .name((String) row[1])
                    .city((String) row[2])
                    .price(row[3] != null ? ((Number) row[3]).doubleValue() : null)
                    .rating(rating)
                    .reviewCount(row[5] != null ? ((Number) row[5]).longValue() : 0L)
                    .image((String) row[6])
                    .score(rating * 2)
                    .build();
        }).toList();
    }

    public List<HotelResponse> getHotelsByOwner(int ownerId, String status) {
        List<Object[]> results;

        if (status != null && !status.isEmpty()) {
            results = hotelRepository.findByOwnerIdAndStatus(ownerId, HotelStatus.valueOf(status));
        } else {
            results = hotelRepository.findHotelsByOwnerWithRating(ownerId);
        }

        List<HotelResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            Hotel hotel = (Hotel) row[0];
            double rating = ((Number) row[1]).doubleValue();
            long reviewCount = ((Number) row[2]).longValue();

            List<String> imageUrls = hotelImageRepository.findByHotel(hotel)
                    .stream()
                    .map(HotelImage::getUrl)
                    .toList();

            responses.add(mapToResponseWithRating(hotel, imageUrls, rating, reviewCount));
        }

        return responses;
    }

    private HotelResponse mapToResponseWithRating(Hotel hotel, List<String> imageUrls, double rating, long reviewCount) {
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
                .rating(rating)
                .reviewCount(reviewCount)
                .build();
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public HotelResponse approveHotel(int id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        if (hotel.getStatus() != HotelStatus.PENDING) {
            throw new AppException(ErrorCode.HOTEL_NOT_PENDING);
        }
        hotel.setStatus(HotelStatus.ACTIVE);
        hotelRepository.save(hotel);
        notificationService.notifyHotelApproved(hotel);
        return mapToResponse(hotel, getHotelImageUrls(hotel));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public HotelResponse rejectHotel(int id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        if (hotel.getStatus() != HotelStatus.PENDING) {
            throw new AppException(ErrorCode.HOTEL_NOT_PENDING);
        }
        hotel.setStatus(HotelStatus.REJECTED);
        hotelRepository.save(hotel);
        notificationService.notifyHotelRejected(hotel);
        return mapToResponse(hotel, getHotelImageUrls(hotel));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public HotelResponse banHotel(int id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        if (hotel.getStatus() == HotelStatus.INACTIVE) {
            throw new AppException(ErrorCode.HOTEL_ALREADY_BANNED);
        }
        hotel.setStatus(HotelStatus.INACTIVE);
        hotelRepository.save(hotel);
        notificationService.notifyHotelBanned(hotel);
        return mapToResponse(hotel, getHotelImageUrls(hotel));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public HotelResponse unbanHotel(int id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        if (hotel.getStatus() == HotelStatus.INACTIVE || hotel.getStatus() == HotelStatus.REJECTED || hotel.getStatus() == HotelStatus.CLOSED) {
            hotel.setStatus(HotelStatus.ACTIVE);
            hotelRepository.save(hotel);
            notificationService.notifyHotelUnbanned(hotel);
            return mapToResponse(hotel, getHotelImageUrls(hotel));
        } else {
            throw new AppException(ErrorCode.HOTEL_NOT_BANNED);
        }
    }

    private List<String> getHotelImageUrls(Hotel hotel) {
        return hotelImageRepository.findByHotel(hotel)
                .stream()
                .map(HotelImage::getUrl)
                .toList();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<HotelResponse> getAllHotelsForAdmin() {
        List<Hotel> hotels = hotelRepository.findAll();
        hotels.sort((h1, h2) -> h2.getCreatedAt().compareTo(h1.getCreatedAt()));
        List<HotelResponse> responses = new ArrayList<>();
        for (Hotel hotel : hotels) {
            responses.add(mapToResponse(hotel, getHotelImageUrls(hotel)));
        }
        return responses;
    }

}