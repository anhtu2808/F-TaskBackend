package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.auth.LoginRequest;
import com.anhtu.ftaskbackend.dto.request.auth.RegisterRequest;
import com.anhtu.ftaskbackend.dto.request.auth.UpdateInformationRequest;
import com.anhtu.ftaskbackend.dto.request.auth.VerifyOtpRequest;
import com.anhtu.ftaskbackend.dto.response.auth.LoginResponse;
import com.anhtu.ftaskbackend.dto.response.auth.SendOTPResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;

public interface AuthService {

    void register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse verify(VerifyOtpRequest verifyOtpRequest);
    UserResponse updateInfo(Long id, UpdateInformationRequest request);
}
