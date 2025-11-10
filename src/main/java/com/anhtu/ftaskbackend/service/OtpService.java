package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.auth.VerifyOtpRequest;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.enums.OtpType;

public interface OtpService {

//    void sendOtp(User user, OtpType type);
    User verifyOtp(String otp);
    void sendSms(String phone, OtpType otpType);
}
