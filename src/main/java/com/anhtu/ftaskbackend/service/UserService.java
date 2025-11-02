package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.response.user.UserInfoResponse;
import com.anhtu.ftaskbackend.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {



    UserInfoResponse getCurrentUser(Long userId);

}
