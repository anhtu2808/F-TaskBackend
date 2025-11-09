package com.anhtu.ftaskbackend.dto.request.admin;

import com.anhtu.ftaskbackend.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminUserUpdateRequest {
    
    String fullName;
    String email;
    String address;
    Gender gender;
    String avatarUrl;
    Boolean isActive;
    
}
