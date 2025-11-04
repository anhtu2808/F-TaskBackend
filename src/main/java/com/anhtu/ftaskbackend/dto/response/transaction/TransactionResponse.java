package com.anhtu.ftaskbackend.dto.response.transaction;

import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.enums.TransactionStatus;
import com.anhtu.ftaskbackend.enums.TransactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionResponse {

    Long id;
    TransactionType type;
    Double amount;
    Double balanceBefore;
    Double balanceAfter;
    String description;
    TransactionStatus status;
    UserResponse user;

}
