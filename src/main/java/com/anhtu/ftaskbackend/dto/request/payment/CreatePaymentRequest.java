package com.anhtu.ftaskbackend.dto.request.payment;

import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.enums.PaymentMethod;
import com.anhtu.ftaskbackend.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequest {

    Double amount;
    PaymentMethod method;
    PaymentStatus status;
    Long bookingId;

}
