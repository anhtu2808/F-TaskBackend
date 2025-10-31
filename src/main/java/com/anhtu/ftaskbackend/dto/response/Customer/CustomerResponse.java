package com.anhtu.ftaskbackend.dto.response.Customer;

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
public class CustomerResponse {

    Long id;
    String phone;
    String email;
    Gender gender;
    String fullName;

}
