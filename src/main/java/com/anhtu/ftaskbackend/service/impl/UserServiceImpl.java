package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.auth.UpdateUserInfoRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserInfoResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.entity.Customer;
import com.anhtu.ftaskbackend.entity.Partner;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.UserMapper;
import com.anhtu.ftaskbackend.repository.CustomerRepository;
import com.anhtu.ftaskbackend.repository.PartnerRepository;
import com.anhtu.ftaskbackend.repository.UserRepository;
import com.anhtu.ftaskbackend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    CustomerRepository customerRepository;
    PartnerRepository partnerRepository;
    UserMapper userMapper;

    @Override
    public UserInfoResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));
        return userMapper.toUserInfoResponse(user);
    }

    @Override
    public UserResponse updateInfo(Long id, UpdateUserInfoRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));
        userMapper.updateInfoToUser(request, user);
        userRepository.save(user);
        switch (user.getRole().getName()){
            case "CUSTOMER" -> customerRepository.save(Customer.builder()
                    .user(user)
                    .build());
            case "PARTNER" -> partnerRepository.save(Partner.builder()
                    .user(user)
                    .isAvailable(true)
//                            .districtIdsJson()
                    .build());
        }
        return userMapper.toUserResponse(user);
    }
}
