// src/main/java/com/project/hotel/service/NotificationService.java
package com.project.hotel.service;

import com.project.hotel.dto.request.CreateNotificationRequest;
import com.project.hotel.dto.response.NotificationResponse;
import com.project.hotel.entity.Hotel;
import com.project.hotel.entity.Notification;
import com.project.hotel.entity.User;
import com.project.hotel.enums.NotificationStatus;
import com.project.hotel.enums.NotificationType;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.NotificationMapper;
import com.project.hotel.repository.NotificationRepository;
import com.project.hotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    // Hàm nội bộ để tạo và lưu thông báo
    private void createAndSaveNotification(User user, String title, String message, String type) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(NotificationType.valueOf(type))
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    // --- Hàm cho Admin gửi thông báo tùy chỉnh ---
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        createAndSaveNotification(user, request.getTitle(), request.getMessage(), request.getType());
        return NotificationResponse.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .build();
    }

    public void notifyHotelApproved(Hotel hotel) {
        String title = "Your hotel has been approved!";
        String message = String.format("Congratulations! Your hotel '%s' has been approved and is now ACTIVE.", hotel.getName());
        createAndSaveNotification(hotel.getOwner(), title, message, "HOTEL_STATUS");
    }

    public void notifyHotelRejected(Hotel hotel) {
        String title = "Your hotel registration was rejected.";
        String message = String.format("We regret to inform you that your hotel '%s' has been REJECTED. Please check your email for details.", hotel.getName());
        createAndSaveNotification(hotel.getOwner(), title, message, "HOTEL_STATUS");
    }

    public void notifyHotelBanned(Hotel hotel) {
        String title = "Your hotel has been banned.";
        String message = String.format("Your hotel '%s' has been set to CLOSED by an administrator.", hotel.getName());
        createAndSaveNotification(hotel.getOwner(), title, message, "HOTEL_STATUS");
    }

    public void notifyHotelUnbanned(Hotel hotel) {
        String title = "Your hotel has been reactivated.";
        String message = String.format("Your hotel '%s' has been reactivated and is now ACTIVE.", hotel.getName());
        createAndSaveNotification(hotel.getOwner(), title, message, "HOTEL_STATUS");
    }

    public List<NotificationResponse> getMyNotifications() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());

        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponse markAsRead(Integer notificationId) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        if (!notification.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        notification.setStatus(NotificationStatus.READ);
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(updatedNotification);
    }

    @Transactional
    public void markAllAsRead() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Notification> unreadNotifications = notificationRepository
                .findByUser_IdAndStatus(user.getId(), NotificationStatus.UNREAD);

        if (unreadNotifications.isEmpty()) {
            return; // Không có gì để làm
        }

        for (Notification notification : unreadNotifications) {
            notification.setStatus(NotificationStatus.READ);
        }

        notificationRepository.saveAll(unreadNotifications);
    }
}