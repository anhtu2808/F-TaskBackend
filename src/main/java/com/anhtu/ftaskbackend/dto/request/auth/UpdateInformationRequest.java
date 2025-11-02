package com.anhtu.ftaskbackend.dto.request.auth;

import com.anhtu.ftaskbackend.entity.Role;
import com.anhtu.ftaskbackend.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInformationRequest {

    Gender gender;
    String fullName;

}
