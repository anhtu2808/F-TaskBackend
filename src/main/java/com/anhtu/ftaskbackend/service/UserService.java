package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.auth.UpdateUserInfoRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminUserFilterRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminUserUpdateRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminUserRoleUpdateRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserInfoResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {

    UserInfoResponse getCurrentUser(Long userId);
    UserResponse updateInfo(Long id, UpdateUserInfoRequest request);
    
    // Admin methods
    Page<UserResponse> getAllUsersForAdmin(AdminUserFilterRequest filter);
    UserResponse adminGetUserById(Long userId);
    UserResponse adminUpdateUser(Long userId, AdminUserUpdateRequest request);
    void adminUpdateUserStatus(Long userId, Boolean isActive);
    void adminUpdateUserRole(Long userId, AdminUserRoleUpdateRequest request);


}
