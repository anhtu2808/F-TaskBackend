package com.anhtu.ftaskbackend.dto.request.booking;

import com.anhtu.ftaskbackend.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterBooking {

    @Builder.Default
    int page = 1;
    @Builder.Default
    int size = 3;
    List<BookingStatus> statuses;
    @Schema(type = "string", format = "date-time", example = "2025-10-30T23:59:59+07:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime fromDate;
    @Schema(type = "string", format = "date-time", example = "2025-10-30T23:59:59+07:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime toDate;
    Double minPrice;
    Double maxPrice;
    String address;
    Long customerId;
    Long partnerId;
    List<Long> joinedBookings;
}
