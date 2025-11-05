package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.auth.LoginRequest;
import com.anhtu.ftaskbackend.dto.request.auth.RegisterRequest;
import com.anhtu.ftaskbackend.dto.request.auth.VerifyOtpRequest;
import com.anhtu.ftaskbackend.dto.response.auth.LoginResponse;
import com.anhtu.ftaskbackend.entity.*;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.UserMapper;
import com.anhtu.ftaskbackend.repository.*;
import com.anhtu.ftaskbackend.service.AuthService;
import com.anhtu.ftaskbackend.service.OtpService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.SIGNER_KEY}")
    String SIGNER_KEY;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    OtpService otpService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    WalletRepository walletRepository;

    @Override
    public void register(RegisterRequest request) {
        if (request.getPhone().length() < 10 || !request.getPhone().startsWith("0")) {
            throw new AppException(ErrorCode.InvalidPhoneNumber);
        }
//        if(request.getEmail() != null){
//            if(userRepository.existsByEmail(request.getEmail())){
//                throw new AppException(ErrorCode.DuplicatedEmail);
//            }
//        }
//        otpService.sendSms(user, OtpType.REGISTER);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByPhone(loginRequest.getPhone())
                .orElseThrow(() -> new AppException(ErrorCode.UserNotFoundByPhone));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.WrongPassword);
        }
        if(!user.getIsActive()){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return LoginResponse.builder()
                .accessToken(generateToken(user))
                .build();
    }

    @Override
    public LoginResponse verify(VerifyOtpRequest request) {
//        User user = otpService.verifyOtp(verifyOtpRequest.getOtp());
//        if (user != userRepository.findByPhone(verifyOtpRequest.getPhone())
//                .orElseThrow(() -> new AppException(ErrorCode.UserNotFoundByPhone))
//        )
//            throw new AppException(ErrorCode.UserNotMatch);
        if(!request.getOtp().equals("123456"))
            throw new AppException(ErrorCode.OtpIsInvalid);
        boolean isNewUser = true;
        User user = userRepository.findByPhone(request.getPhone()).orElse(null);
        if(user != null){
            isNewUser = false;
            user.setIsActive(true);
            userRepository.save(user);
        }
        if(isNewUser){
            user = User.builder()
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(request.getPhone()))  //password bây giờ là sđt để tránh lỗi
                    .wallet(walletRepository.save(new Wallet()))
                    .build();
            user.setRole(roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new AppException(ErrorCode.RoleNotFoundByName)));
            userRepository.save(user);
            switch (request.getRole()) {
                case "CUSTOMER" -> customerRepository.save(Customer.builder()
                        .user(user)
                        .build());
                case "PARTNER" -> partnerRepository.save(Partner.builder()
                        .user(user)
                        .isAvailable(true)
//                            .districtIdsJson()
                        .build());
            }
        }
        return LoginResponse.builder()
                .accessToken(generateToken(user))
                .userId(user.getId())
                .isNewUser(isNewUser)
                .build();
    }

    private String generateToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            Set<Permission> permissions = user.getRole().getPermissions();
            List<String> scopes = permissions.stream()
                    .map(Permission::getName)
                    .collect(Collectors.toList());

            String role = user.getRole().getName();
            String roleId = "";
            String value = "";
            switch (role){
                case "CUSTOMER" -> {
                    roleId = "customerId";
                    value = customerRepository.findByUser_Id(user.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound)).getId().toString();
                }
                case "PARTNER" -> {
                    roleId = "partnerId";
                    value = partnerRepository.findByUser_Id(user.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound)).getId().toString();
                }
            }

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("ftask")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plusSeconds(1209600)))
                    .claim("userId", user.getId())
                    .claim("customerId", user.getCustomer() != null ? user.getCustomer().getId() : null)
                    .claim("partnerId", user.getPartner() != null ? user.getPartner().getId() : null)
                    .claim("role", user.getRole().getName())
                    .claim(roleId, value)
                    .claim("permissions", scopes)
                    .build();

            JWSObject jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

            return jwsObject.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Failed to sign JWT token", e);
        }
    }


}
