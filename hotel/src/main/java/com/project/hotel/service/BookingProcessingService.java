package com.project.hotel.service;

import com.project.hotel.configuration.VNPAYConfig;
import com.project.hotel.entity.Booking;
import com.project.hotel.entity.Payment;
import com.project.hotel.entity.User;
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
import org.springframework.data.redis.core.RedisTemplate;
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
    RedisTemplate<String, Object> redisTemplate;
    EmailService emailService;

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
            log.warn("VNPAY Signature mismatch! Received: {}, Calculated: {}", receivedHash, calculatedHash);
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

            redisTemplate.delete("booking:remind:" + bookingIdStr);
            redisTemplate.delete("booking:expire:" + bookingIdStr);
            log.info("Payment confirmed. Redis keys cleared for booking ID: {}", bookingIdStr);
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

            redisTemplate.delete("booking:remind:" + bookingIdStr);
            redisTemplate.delete("booking:expire:" + bookingIdStr);
            log.info("Payment failed. Redis keys cleared for booking ID: {}", bookingIdStr);
        }
    }

    @Transactional
    public void sendPaymentReminder(Integer bookingId) {
        log.info("Processing payment reminder for booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            log.warn("Booking ID {} not found. Cannot send reminder.", bookingId);
            return;
        }

        if (booking.getStatus() == BookingStatus.PENDING && booking.getPaymentStatus() == PaymentStatus.PENDING) {
            User user = booking.getUser();
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                log.warn("User {} has no email. Cannot send payment reminder for booking {}.",
                        user.getUsername(), bookingId);
                return;
            }

            String subject = "[Hotel Booking] Payment Reminder for Booking #" + booking.getId();
            String retryPaymentUrl = "http://localhost:5173/my-bookings";

            String body = String.format(
                    "Hello %s,\n\n" +
                            "We noticed you have a pending booking at %s (Booking ID: #%d).\n" +
                            "Total amount: %.0f VND\n" +
                            "Check-in date: %s\n\n" +
                            "Please complete your payment. This booking will be automatically cancelled if unpaid within the next 24 hours.\n\n" +
                            "You can review and complete the payment here:\n" +
                            "%s\n\n" +
                            "Thank you,\n" +
                            "Hotel Booking Team",
                    user.getFullName(),
                    booking.getHotel().getName(),
                    booking.getId(),
                    booking.getTotalPrice(),
                    booking.getCheckInDate().toString(),
                    retryPaymentUrl
            );

            emailService.sendSimpleMessage(user.getEmail(), subject, body);
            notificationService.notifyPaymentReminder(booking);

            log.info("Payment reminder email and notification sent for booking ID: {}", bookingId);

        } else {
            log.info("Booking ID {} is no longer in PENDING status. Reminder not sent.", bookingId);
        }
    }

    @Transactional
    public void expirePendingBooking(Integer bookingId) {
        log.info("Processing expiration for booking ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            log.warn("Booking ID {} not found. Cannot expire booking.", bookingId);
            return;
        }

        if (booking.getStatus() == BookingStatus.PENDING && booking.getPaymentStatus() == PaymentStatus.PENDING) {
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setPaymentStatus(PaymentStatus.FAILED);
            booking.setCancellationReason("Payment timeout (Auto cancellation)");

            paymentRepository.findByBookingId(bookingId).ifPresent(payment -> {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            });

            bookingRepository.save(booking);
            log.info("Booking ID {} has been CANCELLED due to payment timeout.", bookingId);

            notificationService.notifyBookingExpired(booking);

            User user = booking.getUser();
            String subject = "[Hotel Booking] Booking #" + booking.getId() + " Cancelled";
            String body = String.format(
                    "Hello %s,\n\n" +
                            "Unfortunately, your booking #%d at %s has been automatically cancelled due to overdue payment.\n\n" +
                            "If you still need a room, please proceed with a new booking.\n\n" +
                            "Thank you.",
                    user.getFullName(),
                    booking.getId(),
                    booking.getHotel().getName()
            );

            emailService.sendSimpleMessage(user.getEmail(), subject, body);

        } else {
            log.info("Booking ID {} is no longer in PENDING status. Expiration skipped.", bookingId);
        }
    }
}
