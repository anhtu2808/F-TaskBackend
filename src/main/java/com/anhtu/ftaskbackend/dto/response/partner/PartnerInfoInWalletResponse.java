package com.anhtu.ftaskbackend.dto.response.partner;

import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartnerInfoInWalletResponse {

    Long id;
    UserResponse user;

}
