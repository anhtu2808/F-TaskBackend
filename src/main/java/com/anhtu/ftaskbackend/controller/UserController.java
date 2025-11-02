package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.auth.UpdateInformationRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserInfoResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user's info (both partner & user can use)")
    public ApiResponse<UserInfoResponse> getCurrentUserInfo() {
        Long userId = JWTHelper.getCurrentUserId();
        UserInfoResponse userInfo = userService.getCurrentUser(userId);
        return ApiResponse.<UserInfoResponse>builder()
                .message("User info retrieved successfully")
                .result(userInfo)
                .build();
    }

    @PutMapping("/update-info/{userId}")
    @Operation(
            summary = "Update information",
            description = "Dành cho các user mới đăng nhập lần đầu"
    )
    public ApiResponse<UserResponse> updateInfo(@RequestBody UpdateInformationRequest request, @PathVariable Long userId) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Update information success")
                .result(userService.updateInfo(userId, request))
                .build();
    }
}
