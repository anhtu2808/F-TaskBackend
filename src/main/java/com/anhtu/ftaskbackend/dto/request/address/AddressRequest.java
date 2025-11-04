package com.anhtu.ftaskbackend.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
    String addressLine;
    
    @NotBlank(message = "District is required")
    String district;
    
    @NotBlank(message = "City is required")
    String city;
    
    String postalCode;
    Double latitude;
    Double longitude;
    
    @Builder.Default
    Boolean isDefault = false;
}
