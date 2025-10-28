// src/main/java/com/project/hotel/service/BookingService.java
package com.project.hotel.service;

import com.project.hotel.configuration.VNPAYConfig;
import com.project.hotel.dto.request.BookingRequest;
import com.project.hotel.dto.request.CancelBookingRequest;
import com.project.hotel.dto.response.BookingDetailResponse;
import com.project.hotel.dto.response.CreatePaymentResponse;
import com.project.hotel.entity.*;
        import com.project.hotel.enums.BookingStatus;
import com.project.hotel.enums.PaymentMethod;
import com.project.hotel.enums.PaymentStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.*;
        import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
        import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class BookingService {

    final BookingRepository bookingRepository;
    final HotelRepository hotelRepository;
    final UserRepository userRepository;
    final RoomRepository roomRepository;
    final BookingRoomRepository bookingRoomRepository;
    final ServiceRepository serviceRepository;
    final BookingServiceRepository bookingServiceRepository;
    final PaymentRepository paymentRepository;
    final HotelImageRepository hotelImageRepository;
    final SpecialPriceRepository specialPriceRepository;
    final RoomTypeRepository roomTypeRepository;
    final HotelImageRepository hotelImageRepo;
    final NotificationService notificationService;

    @Value("${vnpay.tmn-code}") private String tmnCode;
    @Value("${vnpay.hash-secret}") private String hashSecret;
    @Value("${vnpay.url}") private String vnpayUrl;
    @Value("${vnpay.return-url}") private String returnUrl;

    @Transactional
    public CreatePaymentResponse createBookingAndPayment(BookingRequest request, HttpServletRequest httpServletRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        Booking booking = Booking.builder()
                .user(user)
                .hotel(hotel)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(request.getTotalPrice())
                .status(BookingStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        Booking savedBooking = bookingRepository.save(booking);

        for (BookingRequest.RoomBookingDetail roomDetail : request.getRoomsToBook()) {
            List<Room> availableRooms = roomRepository.findAvailableRoomsByRoomTypeAndDate(
                    roomDetail.getRoomTypeId(),
                    request.getCheckInDate(),
                    request.getCheckOutDate(),
                    PageRequest.of(0, roomDetail.getQuantity())
            );

            if (availableRooms.size() < roomDetail.getQuantity()) {
                throw new AppException(ErrorCode.ROOM_NOT_FOUND);
            }

            RoomType roomType = availableRooms.get(0).getRoomType();
            Double pricePerNight;

            List<SpecialPrice> specialPrices = specialPriceRepository.findActiveSpecialPrice(
                    roomType.getId(),
                    request.getCheckInDate()
            );

            if (!specialPrices.isEmpty()) {
                pricePerNight = specialPrices.get(0).getPrice();
            } else {
                pricePerNight = roomType.getPricePerNight();
            }

            for (Room room : availableRooms) {
                bookingRoomRepository.save(BookingRoom.builder()
                        .booking(savedBooking)
                        .room(room)
                        .price(pricePerNight)
                        .build());
            }

            if (roomDetail.getServices() != null && !roomDetail.getServices().isEmpty()) {
                List<com.project.hotel.entity.Service> services = serviceRepository.findAllById(roomDetail.getServices());
                for (com.project.hotel.entity.Service service : services) {
                    bookingServiceRepository.save(com.project.hotel.entity.BookingService.builder()
                            .booking(savedBooking)
                            .service(service)
                            .quantity(1)
                            .price(service.getPrice())
                            .totalPrice(service.getPrice())
                            .build());
                }
            }
        }

        Payment payment = Payment.builder()
                .booking(savedBooking)
                .amount(request.getTotalPrice())
                .method(PaymentMethod.VNPAY)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        notificationService.notifyNewBooking(savedBooking);

        String orderInfo = "Booking for hotel " + hotel.getId() + " - User: " + user.getId();
        long amountInVND = (long) (request.getTotalPrice() * 100);

        Map<String, String> vnp_Params = new TreeMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVND));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(savedBooking.getId()));
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", VNPAYConfig.getIpAddress(httpServletRequest));
        vnp_Params.put("vnp_CreateDate", VNPAYConfig.getCurrentDateVNPAY());

        String queryUrl = VNPAYConfig.getQueryString(vnp_Params);
        String hashData = VNPAYConfig.hmacSHA512(hashSecret, queryUrl);
        String paymentUrl = vnpayUrl + "?" + queryUrl + "&vnp_SecureHash=" + hashData;

        return CreatePaymentResponse.builder()
                .status("OK")
                .message("Booking created, redirecting to payment...")
                .paymentUrl(paymentUrl)
                .build();
    }

    @Transactional(readOnly = true)
    public List<BookingDetailResponse> getMyBookings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        log.info("Fetching bookings for user: {}", username);
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        log.info("Mapping {} bookings to DTO", bookings.size());
        // Lỗi xảy ra ở đây, vì vậy chúng ta sẽ map thủ công
        List<BookingDetailResponse> responses = new ArrayList<>();
        for (Booking booking : bookings) {
            responses.add(mapToBookingDetailResponse(booking));
        }
        return responses;
    }

    @Transactional
    public void cancelBooking(Integer bookingId, CancelBookingRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new AppException(ErrorCode.BOOKING_CANNOT_BE_CANCELLED);
        }
        booking.setStatus(BookingStatus.CANCELLATION_PENDING);
        // Không hoàn tiền vội
        // booking.setPaymentStatus(PaymentStatus.REFUNDED);
        booking.setCancellationReason(request.getReason());
        bookingRepository.save(booking);
        notificationService.notifyCancellationRequest(booking);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public BookingDetailResponse approveCancellation(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.CANCELLATION_PENDING) {
            throw new AppException(ErrorCode.INVALID_ACTION);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setPaymentStatus(PaymentStatus.REFUNDED);
        bookingRepository.save(booking);
        notificationService.notifyCancellationApproved(booking);

        return mapToBookingDetailResponse(booking);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public BookingDetailResponse rejectCancellation(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.CANCELLATION_PENDING) {
            throw new AppException(ErrorCode.INVALID_ACTION);
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        // Lý do hủy vẫn được giữ lại để tham khảo
        // booking.setCancellationReason(null); // (Tùy chọn)
        bookingRepository.save(booking);
        notificationService.notifyCancellationRejected(booking);

        return mapToBookingDetailResponse(booking);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public List<BookingDetailResponse> getBookingsForHotelAdmin(String status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User hotelAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        log.info("Fetching bookings for hotel admin: {}", username);
        List<Booking> bookings;
        if (status != null && !status.isEmpty()) {
            bookings = bookingRepository.findByHotelOwnerIdAndStatusWithDetails(hotelAdmin.getId(), BookingStatus.valueOf(status.toUpperCase()));
        } else {
            bookings = bookingRepository.findByHotelOwnerIdWithDetails(hotelAdmin.getId());
        }

        // Map thủ công
        List<BookingDetailResponse> responses = new ArrayList<>();
        for (Booking booking : bookings) {
            responses.add(mapToBookingDetailResponse(booking));
        }

        log.info("Mapping {} bookings for hotel admin", bookings.size());
        return responses;
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public BookingDetailResponse confirmBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_ACTION);
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.PAID);
        bookingRepository.save(booking);

        notificationService.notifyBookingConfirmed(booking);

        return mapToBookingDetailResponse(booking);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public BookingDetailResponse checkInBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new AppException(ErrorCode.INVALID_ACTION);
        }

        if (!booking.getCheckInDate().isEqual(LocalDate.now())) {
            throw new AppException(ErrorCode.INVALID_CHECKIN_DATE);
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);
        return mapToBookingDetailResponse(booking);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_HOTEL_ADMIN')")
    public BookingDetailResponse checkOutBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new AppException(ErrorCode.INVALID_ACTION);
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
        return mapToBookingDetailResponse(booking);
    }

    private BookingDetailResponse mapToBookingDetailResponse(Booking booking) {
        String mainImage = hotelImageRepo.findFirstByHotelIdAndIsMainTrue(booking.getHotel().getId())
                .map(HotelImage::getUrl)
                .orElse(null);
        List<BookingRoom> bookingRooms = bookingRoomRepository.findByBookingId(booking.getId());
        List<com.project.hotel.entity.BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
        List<BookingDetailResponse.RoomDetail> roomDetails = bookingRooms.stream()
                .map(br -> BookingDetailResponse.RoomDetail.builder()
                        .roomName(loadRoomTypeName(br.getRoom()))
                        .roomNumber(br.getRoom().getRoomNumber())
                        .price(br.getPrice())
                        .build())
                .collect(Collectors.toList());
        List<BookingDetailResponse.ServiceDetail> serviceDetails = bookingServices.stream()
                .map(bs -> BookingDetailResponse.ServiceDetail.builder()
                        .name(bs.getService().getName())
                        .price(bs.getPrice())
                        .quantity(bs.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return BookingDetailResponse.builder()
                .id(booking.getId())
                .hotel(BookingDetailResponse.HotelInfo.builder()
                        .id(booking.getHotel().getId())
                        .name(booking.getHotel().getName())
                        .address(booking.getHotel().getAddress())
                        .image(mainImage)
                        .build())
                .user(BookingDetailResponse.UserInfo.builder()
                        .id(booking.getUser().getId())
                        .username(booking.getUser().getUsername())
                        .fullName(booking.getUser().getFullName())
                        .email(booking.getUser().getEmail())
                        .build())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .paymentStatus(booking.getPaymentStatus().name())
                .createdAt(booking.getCreatedAt())
                .cancellationReason(booking.getCancellationReason())
                .rooms(roomDetails)
                .services(serviceDetails)
                .build();
    }
    private String loadRoomTypeName(Room room) {
        RoomType rt = roomTypeRepository.findById(room.getRoomType().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        return rt.getName();
    }
}