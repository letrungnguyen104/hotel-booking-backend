// src/main/java/com/project/hotel/service/BookingProcessingService.java
package com.project.hotel.service;

import com.project.hotel.configuration.VNPAYConfig;
import com.project.hotel.entity.Booking;
import com.project.hotel.entity.Payment;
import com.project.hotel.enums.BookingStatus;
import com.project.hotel.enums.PaymentStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.BookingRepository;
import com.project.hotel.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingProcessingService {

    BookingRepository bookingRepository;
    PaymentRepository paymentRepository;
    NotificationService notificationService;

    @Value("${vnpay.hash-secret}")
    @NonFinal
    private String hashSecret;

    public void verifySignature(Map<String, String> allParams) {
        String receivedHash = allParams.get("vnp_SecureHash");
        if (receivedHash == null || receivedHash.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_SIGNATURE);
        }

        Map<String, String> fields = new TreeMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().equals("vnp_SecureHash") || entry.getKey().equals("vnp_SecureHashType")) {
                continue;
            }
            fields.put(entry.getKey(), entry.getValue());
        }

        String hashData = VNPAYConfig.getQueryString(fields);
        String calculatedHash = VNPAYConfig.hmacSHA512(hashSecret, hashData);

        if (!receivedHash.equals(calculatedHash)) {
            log.warn("VNPAY Signature Mismatch! Received: {}, Calculated: {}", receivedHash, calculatedHash);
            throw new AppException(ErrorCode.INVALID_SIGNATURE);
        }
    }

    @Transactional
    public void confirmPayment(String bookingIdStr, String transactionNo) {
        int bookingId = Integer.parseInt(bookingIdStr);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        if (booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPaymentStatus(PaymentStatus.PAID);

            payment.setStatus(PaymentStatus.PAID);
            payment.setTransactionId(transactionNo);
            payment.setPaidAt(LocalDateTime.now());

            bookingRepository.save(booking);
            paymentRepository.save(payment);
            notificationService.notifyPaymentSuccess(booking);
            notificationService.notifyNewBooking(booking);
        }
    }

    @Transactional
    public void failPayment(String bookingIdStr, String transactionNo) {
        int bookingId = Integer.parseInt(bookingIdStr);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setPaymentStatus(PaymentStatus.FAILED);

            payment.setStatus(PaymentStatus.FAILED);
            payment.setTransactionId(transactionNo);

            bookingRepository.save(booking);
            paymentRepository.save(payment);

            notificationService.notifyPaymentFailed(booking);
        }
    }
}