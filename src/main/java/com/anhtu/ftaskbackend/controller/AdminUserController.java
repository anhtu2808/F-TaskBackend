package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.admin.AdminUserFilterRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminUserUpdateRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminUserRoleUpdateRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Admin Users", description = "Admin user management APIs")
public class AdminUserController {

    UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all users with filters", description = "Get paginated list of users with comprehensive filtering options for admin")
    public ApiResponse<Page<UserResponse>> getAllUsers(@ParameterObject AdminUserFilterRequest filter) {
        return ApiResponse.<Page<UserResponse>>builder()
                .code(200)
                .message("Users retrieved successfully")
                .result(userService.getAllUsersForAdmin(filter))
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user details", description = "Get detailed information about a specific user")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("User details retrieved successfully")
                .result(userService.adminGetUserById(id))
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user information", description = "Admin can update user profile information")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("User updated successfully")
                .result(userService.adminUpdateUser(id, request))
                .build();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user status", description = "Admin can activate or deactivate user account")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {
        userService.adminUpdateUserStatus(id, isActive);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("User status updated successfully")
                .build();
    }

    @PutMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user role", description = "Admin can change user role")
    public ApiResponse<Void> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserRoleUpdateRequest request) {
        userService.adminUpdateUserRole(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("User role updated successfully")
                .build();
    }
}
