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
public class AdminRevenueStatsResponse {
    
    Double totalRevenue;
    Double totalPlatformFee;
    LocalDate fromDate;
    LocalDate toDate;
    List<DailyRevenueStats> dailyStats;
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DailyRevenueStats {
        LocalDate date;
        Double revenue;
        Double platformFee;
        Long bookingCount;
    }
    
}
