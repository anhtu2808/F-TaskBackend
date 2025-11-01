package com.anhtu.ftaskbackend.dto.response.Customer;

import com.anhtu.ftaskbackend.entity.Customer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {

    Long id;
    String addressLine;
    String district;
    String city;
    String postalCode;
    Double latitude;
    Double longitude;

}
