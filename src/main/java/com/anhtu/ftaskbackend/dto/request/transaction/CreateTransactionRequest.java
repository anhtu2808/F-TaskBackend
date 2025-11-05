package com.anhtu.ftaskbackend.dto.request.transaction;

import com.anhtu.ftaskbackend.entity.BookingPartner;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.enums.TransactionStatus;
import com.anhtu.ftaskbackend.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTransactionRequest {

    TransactionType type;
    Double amount;
    String description;
    Long bookingPartnerId;

}
