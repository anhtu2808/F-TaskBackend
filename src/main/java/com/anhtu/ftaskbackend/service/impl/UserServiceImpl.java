package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.auth.UpdateUserInfoRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminUserFilterRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminUserUpdateRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminUserRoleUpdateRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserInfoResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.entity.Customer;
import com.anhtu.ftaskbackend.entity.Partner;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.entity.Wallet;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.UserMapper;
import com.anhtu.ftaskbackend.repository.CustomerRepository;
import com.anhtu.ftaskbackend.repository.PartnerRepository;
import com.anhtu.ftaskbackend.repository.UserRepository;
import com.anhtu.ftaskbackend.repository.WalletRepository;
import com.anhtu.ftaskbackend.repository.RoleRepository;
import com.anhtu.ftaskbackend.repository.specification.AdminUserSpecification;
import com.anhtu.ftaskbackend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    WalletRepository walletRepository;
    RoleRepository roleRepository;

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
        return userMapper.toUserResponse(user);
    }

    // Admin methods implementation
    @Override
    public Page<UserResponse> getAllUsersForAdmin(AdminUserFilterRequest filter) {
        var spec = AdminUserSpecification.filter(filter);
        
        // Create sort
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(filter.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
            filter.getSortBy()
        );
        
        var pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        return userRepository.findAll(spec, pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    public UserResponse adminGetUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse adminUpdateUser(Long userId, AdminUserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));

        // Update user fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void adminUpdateUserStatus(Long userId, Boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));

        user.setIsActive(isActive);
        userRepository.save(user);
    }

    @Override
    public void adminUpdateUserRole(Long userId, AdminUserRoleUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));

        var role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.RoleNotFound));

        user.setRole(role);
        userRepository.save(user);
    }
}
