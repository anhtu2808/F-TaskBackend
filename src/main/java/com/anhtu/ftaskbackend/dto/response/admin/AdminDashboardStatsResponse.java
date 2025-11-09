package com.anhtu.ftaskbackend.dto.response.admin;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminDashboardStatsResponse {
    
    Long totalUsers;
    Long totalCustomers;
    Long totalPartners;
    Long totalBookings;
    Long totalCompletedBookings;
    Long totalCancelledBookings;
    Long totalPendingBookings;
    Long totalServiceCatalogs;
    Long totalServiceVariants;
    Long totalReviews;
    Double totalRevenue;
    Double totalPlatformFee;
    Double averageRating;
    
}
