package com.project.hotel.mapper;

import com.project.hotel.dto.response.NotificationResponse;
import com.project.hotel.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toNotificationResponse(Notification notification);
}