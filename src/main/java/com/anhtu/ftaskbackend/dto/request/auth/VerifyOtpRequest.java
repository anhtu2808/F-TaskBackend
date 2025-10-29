package com.anhtu.ftaskbackend.dto.request.auth;

import com.anhtu.ftaskbackend.enums.OtpType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {

    String otp;
    OtpType otpType;

}
