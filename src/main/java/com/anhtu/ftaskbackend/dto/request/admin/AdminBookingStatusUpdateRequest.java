package com.anhtu.ftaskbackend.dto.request.admin;

import com.anhtu.ftaskbackend.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminBookingStatusUpdateRequest {
    
    @NotNull(message = "Status is required")
    BookingStatus status;
    
    String reason;
    
}
