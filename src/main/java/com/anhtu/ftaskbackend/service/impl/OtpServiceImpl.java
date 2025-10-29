package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.auth.VerifyOtpRequest;
import com.anhtu.ftaskbackend.entity.Otp;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.enums.OtpType;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.repository.OtpRepository;
import com.anhtu.ftaskbackend.service.OtpService;
import com.anhtu.ftaskbackend.thirdParty.SpeedSMSAPI;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpServiceImpl implements OtpService {

    @Autowired
    OtpRepository otpRepository;
    @Value("${speedSMS.token}")
    String speedSMSToken;

    public void sendOtp(User user, OtpType type) {
        try {
            String otp = String.format("%06d", new Random().nextInt(999999));

            otpRepository.save(Otp.builder()
                    .otpType(type)
                    .expiredAt(LocalDateTime.now().plusMinutes(5))
                    .otpCode(otp)
                    .user(user)
                    .build());

            String phoneNumber = user.getPhone().replaceAll("[^0-9]", "");
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "84" + phoneNumber.substring(1);
            } else if (phoneNumber.startsWith("+84")) {
                phoneNumber = phoneNumber.substring(1);
            }

            SpeedSMSAPI api = new SpeedSMSAPI(speedSMSToken);

            String content = String.format(
                    "Mã xác thực OTP của bạn là %s. Mã sẽ hết hạn sau 5 phút.", otp);

            int smsType = 2;
            String sender = "";

            String response = api.sendSMS(phoneNumber, content, smsType, sender);
            System.out.println("[SpeedSMS] Response: " + response);

            System.out.println("Sent OTP " + otp + " to " + phoneNumber);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS via SpeedSMS", e);
        }
    }

    @Override
    public User verifyOtp(VerifyOtpRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Otp otp = otpRepository.findByOtpCodeAndIsUsedFalse(request.getOtp())
                .orElseThrow(() -> new AppException(ErrorCode.OtpNotFoundByCode));
        if(!otp.getOtpType().equals(request.getOtpType()))
            throw new AppException(ErrorCode.OtpIsNotSuitable);
        if(otp.getExpiredAt().isBefore(now))
            throw new AppException(ErrorCode.OtpIsExpired);
        otp.setIsUsed(true);
        otpRepository.save(otp);
        return otp.getUser();
    }

}
