package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.auth.UpdateInformationRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserInfoResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {

    UserInfoResponse getCurrentUser(Long userId);
    UserResponse updateInfo(Long id, UpdateInformationRequest request);


}
