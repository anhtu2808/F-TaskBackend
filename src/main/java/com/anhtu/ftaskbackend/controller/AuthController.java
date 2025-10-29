package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.auth.LoginRequest;
import com.anhtu.ftaskbackend.dto.request.auth.RegisterRequest;
import com.anhtu.ftaskbackend.dto.request.auth.VerifyOtpRequest;
import com.anhtu.ftaskbackend.dto.response.auth.LoginResponse;
import com.anhtu.ftaskbackend.service.AuthService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ApiResponse.<Void>builder()
                .message("Register success")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ApiResponse.<LoginResponse>builder()
                .message("Login success")
                .result(authService.login(loginRequest))
                .build();
    }

    @PostMapping("/verify")
    public ApiResponse<LoginResponse> verify(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        return ApiResponse.<LoginResponse>builder()
                .message("Verify success")
                .result(authService.verify(verifyOtpRequest))
                .build();
    }

}
