package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.response.user.UserInfoResponse;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.mapper.UserMapper;
import com.anhtu.ftaskbackend.repository.UserRepository;
import com.anhtu.ftaskbackend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    @Override
    public UserInfoResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));
        return userMapper.toUserInfoResponse(user);
    }
}
