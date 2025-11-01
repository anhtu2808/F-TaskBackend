package com.anhtu.ftaskbackend.dto.response.booking;

import com.anhtu.ftaskbackend.dto.response.partner.PartnerResponse;
import com.anhtu.ftaskbackend.enums.BookingPartnerStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingPartnerResponse {

    Long id;
    Double partnerEarnings;
    BookingPartnerStatus status;
    String cancelReason;
    LocalDate joinAt;
    PartnerResponse partner;

}
