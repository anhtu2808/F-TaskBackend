package com.anhtu.ftaskbackend.dto.response.payment;

import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.enums.PaymentMethod;
import com.anhtu.ftaskbackend.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {

    Long id;
    Double amount;
    PaymentMethod method;
    PaymentStatus status;
    String gatewayTransactionCode;
    LocalDateTime paymentAt;
    BookingResponse booking;
}
