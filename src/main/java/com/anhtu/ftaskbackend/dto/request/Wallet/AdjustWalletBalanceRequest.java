package com.anhtu.ftaskbackend.dto.request.Wallet;

import com.anhtu.ftaskbackend.dto.response.partner.PartnerInfoInWalletResponse;
import com.anhtu.ftaskbackend.enums.TransactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdjustWalletBalanceRequest {

    TransactionType type;
    Double amount;
    Long bookingPartnerId;
    Long bookingId;

}
