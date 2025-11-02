package com.anhtu.ftaskbackend.dto.request.address;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
    Long customerId;
    String addressLine;
    String district;
    String city;
    String postalCode;
    Double latitude;
    Double longitude;
    Boolean isDefault;
}
