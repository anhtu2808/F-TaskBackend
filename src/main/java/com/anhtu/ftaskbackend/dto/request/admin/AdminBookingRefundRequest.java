package com.anhtu.ftaskbackend.dto.request.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminBookingRefundRequest {
    
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Refund amount must be greater than 0")
    Double refundAmount;
    
    String reason;
    
}
