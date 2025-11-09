package com.anhtu.ftaskbackend.dto.request.admin;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminPartnerDistrictsRequest {
    
    @NotEmpty(message = "District IDs are required")
    List<Long> districtIds;
    
}
