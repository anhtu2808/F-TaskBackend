package com.anhtu.ftaskbackend.dto.response.user;

import com.anhtu.ftaskbackend.entity.Role;
import com.anhtu.ftaskbackend.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    Long id;
    String username;
    String phone;
    String email;
    Gender gender;
    String idCard;
    String fullName;
    String role;

}
