package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.NotificationService;
import lombok.Builder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Builder
@RequestMapping("/notifications")
public class NotificationController {
    NotificationService notificationService;

    @PostMapping("/test")
    public ApiResponse<Void> testNotification(@RequestParam("fcmToken") String fcmToken) {
//        Long userId = JWTHelper.getCurrentUserId();
        Long userId = 1L;
        notificationService.sendNotification(userId, fcmToken);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Test notification sent successfully to token: " + fcmToken)
                .build();
    }
}
