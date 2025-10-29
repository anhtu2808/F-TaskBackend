package com.anhtu.ftaskbackend.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    String firstName;
    String lastName;
    String idCard;
    String phone;
    String email;
    String hashedPassword;

}
