package com.anhtu.ftaskbackend.dto.response.address;

import com.anhtu.ftaskbackend.entity.Address;
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
    Long customerId;
    String addressLine;
    String district;
    String city;
    String postalCode;
    Double latitude;
    Double longitude;
    Boolean isDefault;

    public AddressResponse fromEntity(Address entity) {
        if (entity == null) return null;

        return AddressResponse.builder()
                .id(entity.getId())
                .customerId(entity.getCustomer() != null ? entity.getCustomer().getId() : null)
                .addressLine(entity.getAddressLine())
                .district(entity.getDistrict())
                .city(entity.getCity())
                .postalCode(entity.getPostalCode())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .isDefault(entity.getIsDefault())
                .build();
    }
}
