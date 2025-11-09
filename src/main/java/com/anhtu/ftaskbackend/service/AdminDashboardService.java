package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.response.admin.AdminDashboardStatsResponse;
import com.anhtu.ftaskbackend.dto.response.admin.AdminRevenueStatsResponse;
import com.anhtu.ftaskbackend.dto.response.admin.AdminBookingTrendResponse;

import java.time.LocalDate;

public interface AdminDashboardService {
    
    AdminDashboardStatsResponse getOverallStats();
    
    AdminRevenueStatsResponse getRevenueStats(LocalDate fromDate, LocalDate toDate);
    
    AdminBookingTrendResponse getBookingsTrend(LocalDate fromDate, LocalDate toDate, String period);
    
}
