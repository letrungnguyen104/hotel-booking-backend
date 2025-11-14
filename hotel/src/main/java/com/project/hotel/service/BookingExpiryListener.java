package com.project.hotel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingExpiryListener implements MessageListener {

    private final BookingProcessingService bookingProcessingService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        log.info("Redis key expired: {}", expiredKey);

        try {
            if (expiredKey.startsWith("booking:remind:")) {
                String bookingIdStr = expiredKey.substring("booking:remind:".length());
                Integer bookingId = Integer.parseInt(bookingIdStr);
                log.info("Sending payment reminder for booking ID: {}", bookingId);
                bookingProcessingService.sendPaymentReminder(bookingId);

            } else if (expiredKey.startsWith("booking:expire:")) {
                String bookingIdStr = expiredKey.substring("booking:expire:".length());
                Integer bookingId = Integer.parseInt(bookingIdStr);
                log.info("Expiring pending booking ID: {}", bookingId);
                bookingProcessingService.expirePendingBooking(bookingId);
            }
        } catch (Exception e) {
            log.error("Error processing expired key: " + expiredKey, e);
        }
    }
}