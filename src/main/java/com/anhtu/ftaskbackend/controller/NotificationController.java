package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.response.notification.NotificationResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequestMapping("/notifications")
@Tag(name = "Notification", description = "Notification management APIs")
public class NotificationController {
    
    NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get all notifications of current user")
    public ApiResponse<List<NotificationResponse>> getUserNotifications() {
        Long userId = JWTHelper.getCurrentUserId();
        List<NotificationResponse> response = notificationService.getUserNotifications(userId);
        return ApiResponse.<List<NotificationResponse>>builder()
                .message("Get notifications successfully")
                .result(response)
                .build();
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = JWTHelper.getCurrentUserId();
        Long count = notificationService.getUnreadCount(userId);
        return ApiResponse.<Long>builder()
                .message("Get unread count successfully")
                .result(count)
                .build();
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read")
    public ApiResponse<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.<Void>builder()
                .message("Notification marked as read")
                .build();
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ApiResponse<Void> markAllAsRead() {
        Long userId = JWTHelper.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ApiResponse.<Void>builder()
                .message("All notifications marked as read")
                .build();
    }

    @PostMapping("/test")
    @Operation(summary = "Test send notification")
    public ApiResponse<Void> testNotification(@RequestParam("fcmToken") String fcmToken) {
        Long userId = JWTHelper.getCurrentUserId();
        notificationService.sendNotification(userId, fcmToken);
        return ApiResponse.<Void>builder()
                .message("Test notification sent successfully")
                .build();
    }
}
