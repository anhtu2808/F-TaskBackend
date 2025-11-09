package com.anhtu.ftaskbackend.dto.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminPartnerStatusUpdateRequest {
    
    @NotNull(message = "Available status is required")
    Boolean isAvailable;
    
}
