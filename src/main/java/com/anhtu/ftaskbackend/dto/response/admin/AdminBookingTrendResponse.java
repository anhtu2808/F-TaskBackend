package com.anhtu.ftaskbackend.dto.response.admin;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminBookingTrendResponse {
    
    LocalDate fromDate;
    LocalDate toDate;
    String period; // DAILY, WEEKLY, MONTHLY
    List<BookingTrendData> trendData;
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BookingTrendData {
        LocalDate date;
        Long totalBookings;
        Long completedBookings;
        Long cancelledBookings;
        Long pendingBookings;
    }
    
}
