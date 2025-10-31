package com.anhtu.ftaskbackend.dto.request.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookingRequest {

    Long variantId;
    Long addressId;
    LocalDateTime startAt;
    String customerNote;

}
