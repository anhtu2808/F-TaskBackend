package com.anhtu.ftaskbackend.dto.response.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenerateQRCodeResponse {

    String qrToken;

}

