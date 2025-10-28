package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.auth.LoginRequest;
import com.anhtu.ftaskbackend.dto.request.auth.RegisterRequest;
import com.anhtu.ftaskbackend.dto.request.auth.VerifyOtpRequest;
import com.anhtu.ftaskbackend.dto.response.auth.LoginResponse;
import com.anhtu.ftaskbackend.entity.Permission;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.enums.AccountType;
import com.anhtu.ftaskbackend.enums.OtpType;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.UserMapper;
import com.anhtu.ftaskbackend.repository.RoleRepository;
import com.anhtu.ftaskbackend.repository.UserRepository;
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

    @Override
    public void register(RegisterRequest request) {
        if(request.getEmail() != null){
            if(userRepository.existsByEmail(request.getEmail())){
                throw new AppException(ErrorCode.DuplicatedEmail);
            }
        }
        if(request.getPhone() != null){
            if(userRepository.existsByPhone(request.getPhone())){
                throw new AppException(ErrorCode.DuplicatedPhone);
            }
        }
        User user = userMapper.registerRequestToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        switch (request.getRoleName()){
            case "CUSTOMER" -> user.setRole(roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new AppException(ErrorCode.RoleNotFoundByName)));
            case "PARTNER" -> user.setRole(roleRepository.findByName("PARTNER")
                    .orElseThrow(() -> new AppException(ErrorCode.RoleNotFoundByName)));
        }
        user.setIsActive(false);
        userRepository.save(user);
        otpService.sendOtp(user, OtpType.REGISTER);
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
    public LoginResponse verify(VerifyOtpRequest verifyOtpRequest) {
        User user = otpService.verifyOtp(verifyOtpRequest);
        return user != null ?
                LoginResponse.builder()
                        .accessToken(generateToken(user))
                        .build() :
                null;
    }

    private String generateToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            Set<Permission> permissions = user.getRole().getPermissions();
            List<String> scopes = permissions.stream()
                    .map(Permission::getName)
                    .collect(Collectors.toList());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("ftask")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plusSeconds(900)))
                    .claim("role", user.getRole().getName())
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
