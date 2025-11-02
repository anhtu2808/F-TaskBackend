package com.anhtu.ftaskbackend.dto.response.Customer;

import com.anhtu.ftaskbackend.dto.response.address.AddressResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.entity.Role;
import com.anhtu.ftaskbackend.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerResponse {

    Long id;
    List<AddressResponse> addresses;
    UserResponse user;

}
