package com.anhtu.ftaskbackend.dto.response.wallet;

import com.anhtu.ftaskbackend.dto.response.partner.PartnerInfoInWalletResponse;
import com.anhtu.ftaskbackend.dto.response.partner.PartnerResponse;
import com.anhtu.ftaskbackend.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletResponse {

    Long id;
    Double balance;
    Double totalEarned;
    Double totalWithdrawn;
    PartnerInfoInWalletResponse partner;


}
