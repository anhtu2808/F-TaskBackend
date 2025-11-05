package com.anhtu.ftaskbackend.dto.request.auth;

import com.anhtu.ftaskbackend.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoRequest {

    Gender gender;
    String fcmToken;
    String fullName;

}
