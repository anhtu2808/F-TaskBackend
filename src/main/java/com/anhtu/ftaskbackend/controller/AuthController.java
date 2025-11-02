package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.auth.LoginRequest;
import com.anhtu.ftaskbackend.dto.request.auth.RegisterRequest;
import com.anhtu.ftaskbackend.dto.request.auth.UpdateInformationRequest;
import com.anhtu.ftaskbackend.dto.request.auth.VerifyOtpRequest;
import com.anhtu.ftaskbackend.dto.response.auth.LoginResponse;
import com.anhtu.ftaskbackend.dto.response.auth.SendOTPResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.service.AuthService;
import com.anhtu.ftaskbackend.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {

    @Autowired
    AuthService authService;
    @Autowired
    OtpService otpService;

    @PostMapping("/send-otp")
    @Operation(
            summary = "Send OTP to phone number",
            description = "Hàm này khi hoàn tất sẽ send được tại giờ đang giới hạn"
    )
    public ApiResponse<Void> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ApiResponse.<Void>builder()
                .message("Send OTP success")
                .build();
    }

//    @PostMapping("/login")
//    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
//        return ApiResponse.<LoginResponse>builder()
//                .message("Login success")
//                .result(authService.login(loginRequest))
//                .build();
//    }

    @Operation(
            summary = "Verify OTP",
            description = "Default OTP: 123456, Phone number lấy lại phone ở hàm send OTP, Role nếu là app cho người dùng" +
                    " thì là CUSTOMER còn app cho partner thì là PARTNER\n" +
                    "Response trả về accessToken và các biến isNewUser để check và xác định màn hình tiếp theo\n" +
                    "Nếu là new user sẽ có trả thêm user id để tiện sử dụng update thông tin"
    )
    @PostMapping("/verify-otp")
    public ApiResponse<LoginResponse> verify(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        return ApiResponse.<LoginResponse>builder()
                .message("Verify success")
                .result(authService.verify(verifyOtpRequest))
                .build();
    }

    @PutMapping("/update-info/{userId}")
    @Operation(
            summary = "Update information",
            description = "Dành cho các user mới đăng nhập lần đầu"
    )
    public ApiResponse<UserResponse> updateInfo(@RequestBody UpdateInformationRequest request, @PathVariable Long userId) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Update information success")
                .result(authService.updateInfo(userId, request))
                .build();
    }

}
