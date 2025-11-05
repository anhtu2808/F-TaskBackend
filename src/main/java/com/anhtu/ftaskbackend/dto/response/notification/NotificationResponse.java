package com.anhtu.ftaskbackend.dto.response.notification;

import com.anhtu.ftaskbackend.enums.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    
    Long id;
    Long userId;
    Long bookingId;
    NotificationType type;
    String title;
    String message;
    Boolean isRead;
    LocalDateTime createdAt;
}

