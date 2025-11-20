package com.project.hotel.service;

import com.project.hotel.dto.response.PaymentHistoryResponse;
import com.project.hotel.entity.Payment;
import com.project.hotel.entity.BookingRoom;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.PaymentRepository;
import com.project.hotel.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    PaymentRepository paymentRepository;
    UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PaymentHistoryResponse> getPaymentHistoryForOwner() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer ownerId = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)).getId();

        List<Payment> payments = paymentRepository.findPaymentsByHotelOwner(ownerId);

        return payments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private PaymentHistoryResponse mapToResponse(Payment payment) {
        String roomInfo = "Unknown Room";
        var bookingRooms = payment.getBooking().getBookingRooms();
        if (bookingRooms != null && !bookingRooms.isEmpty()) {
            BookingRoom firstRoom = bookingRooms.stream().findFirst().orElse(null);
            if (firstRoom != null) {
                roomInfo = firstRoom.getRoom().getRoomType().getName();
            }
            if (bookingRooms.size() > 1) {
                roomInfo += " & others";
            }
        }

        return PaymentHistoryResponse.builder()
                .paymentId(payment.getId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .paymentDate(payment.getPaidAt() != null ? payment.getPaidAt() : payment.getCreatedAt())
                .bookingId(payment.getBooking().getId())
                .customerName(payment.getBooking().getUser().getFullName())
                .customerEmail(payment.getBooking().getUser().getEmail())
                .roomTypeName(roomInfo)
                .checkInDate(payment.getBooking().getCheckInDate().atStartOfDay())
                .checkOutDate(payment.getBooking().getCheckOutDate().atStartOfDay())
                .build();
    }
}