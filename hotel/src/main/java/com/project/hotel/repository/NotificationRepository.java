package com.project.hotel.repository;

import com.project.hotel.entity.Notification;
import com.project.hotel.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByUser_IdAndStatus(Integer userId, NotificationStatus status);
}
