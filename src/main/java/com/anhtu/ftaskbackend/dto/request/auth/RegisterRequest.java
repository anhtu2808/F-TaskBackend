package com.anhtu.ftaskbackend.dto.request.auth;

import com.anhtu.ftaskbackend.enums.AccountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    String fullName;
    String idCard;
    String phone;
    String email;
    String referralCode;
    String password;
    String role;

}
