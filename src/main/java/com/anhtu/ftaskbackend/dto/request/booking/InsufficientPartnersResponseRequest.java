package com.anhtu.ftaskbackend.dto.request.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InsufficientPartnersResponseRequest {

    Boolean cancel; // true = cancel booking, false = continue with available partners

}

