package com.anhtu.ftaskbackend.dto.request.booking;

import com.anhtu.ftaskbackend.enums.BookingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterBookingParams {

    @Builder.Default
    int page = 1;
    @Builder.Default
    int size = 3;
    BookingStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime fromDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime toDate;
    Double minPrice;
    Double maxPrice;
    String address;
}
