package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.response.admin.AdminDashboardStatsResponse;
import com.anhtu.ftaskbackend.dto.response.admin.AdminRevenueStatsResponse;
import com.anhtu.ftaskbackend.dto.response.admin.AdminBookingTrendResponse;
import com.anhtu.ftaskbackend.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Admin Dashboard", description = "Admin dashboard statistics and analytics APIs")
public class AdminDashboardController {

    AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get overall dashboard statistics", description = "Get comprehensive statistics including users, bookings, revenue, and ratings")
    public ApiResponse<AdminDashboardStatsResponse> getOverallStats() {
        return ApiResponse.<AdminDashboardStatsResponse>builder()
                .code(200)
                .message("Dashboard statistics retrieved successfully")
                .result(adminDashboardService.getOverallStats())
                .build();
    }

    @GetMapping("/revenue")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get revenue statistics", description = "Get revenue statistics with daily breakdown for specified date range")
    public ApiResponse<AdminRevenueStatsResponse> getRevenueStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ApiResponse.<AdminRevenueStatsResponse>builder()
                .code(200)
                .message("Revenue statistics retrieved successfully")
                .result(adminDashboardService.getRevenueStats(fromDate, toDate))
                .build();
    }

    @GetMapping("/bookings-trend")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get booking trends", description = "Get booking trends by status for specified date range and period")
    public ApiResponse<AdminBookingTrendResponse> getBookingsTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "DAILY") String period) {
        return ApiResponse.<AdminBookingTrendResponse>builder()
                .code(200)
                .message("Booking trends retrieved successfully")
                .result(adminDashboardService.getBookingsTrend(fromDate, toDate, period))
                .build();
    }
}
