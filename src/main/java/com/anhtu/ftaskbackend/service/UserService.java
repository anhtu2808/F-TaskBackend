package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.auth.UpdateUserInfoRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserInfoResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;

public interface UserService {

    UserInfoResponse getCurrentUser(Long userId);
    UserResponse updateInfo(Long id, UpdateUserInfoRequest request);


}
