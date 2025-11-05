package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.notification.NotificationResponse;
import com.anhtu.ftaskbackend.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "createAt", target = "createdAt")
    NotificationResponse toResponse(Notification entity);
}

