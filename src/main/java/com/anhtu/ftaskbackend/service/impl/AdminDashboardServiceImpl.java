package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.response.admin.AdminDashboardStatsResponse;
import com.anhtu.ftaskbackend.dto.response.admin.AdminRevenueStatsResponse;
import com.anhtu.ftaskbackend.dto.response.admin.AdminBookingTrendResponse;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.repository.*;
import com.anhtu.ftaskbackend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    UserRepository userRepository;
    CustomerRepository customerRepository;
    PartnerRepository partnerRepository;
    BookingRepository bookingRepository;
    ServiceCatalogRepository serviceCatalogRepository;
    ServiceCatalogVariantRepository serviceCatalogVariantRepository;
    ReviewRepository reviewRepository;

    @Override
    public AdminDashboardStatsResponse getOverallStats() {
        Long totalUsers = userRepository.count();
        Long totalCustomers = customerRepository.count();
        Long totalPartners = partnerRepository.count();
        Long totalBookings = bookingRepository.count();
        
        Long totalCompletedBookings = bookingRepository.countByStatus(BookingStatus.COMPLETED);
        Long totalCancelledBookings = bookingRepository.countByStatus(BookingStatus.CANCELLED);
        Long totalPendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        
        Long totalServiceCatalogs = serviceCatalogRepository.count();
        Long totalServiceVariants = serviceCatalogVariantRepository.count();
        Long totalReviews = reviewRepository.count();
        
        // Calculate total revenue and platform fee from completed bookings
        Double totalRevenue = bookingRepository.findByStatus(BookingStatus.COMPLETED)
                .stream()
                .mapToDouble(booking -> booking.getTotalPrice())
                .sum();
                
        Double totalPlatformFee = bookingRepository.findByStatus(BookingStatus.COMPLETED)
                .stream()
                .mapToDouble(booking -> booking.getPlatformFee())
                .sum();
        
        // Calculate average rating
        Double averageRating = reviewRepository.findAll()
                .stream()
                .mapToDouble(review -> review.getRating())
                .average()
                .orElse(0.0);

        return AdminDashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalCustomers(totalCustomers)
                .totalPartners(totalPartners)
                .totalBookings(totalBookings)
                .totalCompletedBookings(totalCompletedBookings)
                .totalCancelledBookings(totalCancelledBookings)
                .totalPendingBookings(totalPendingBookings)
                .totalServiceCatalogs(totalServiceCatalogs)
                .totalServiceVariants(totalServiceVariants)
                .totalReviews(totalReviews)
                .totalRevenue(totalRevenue)
                .totalPlatformFee(totalPlatformFee)
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .build();
    }

    @Override
    public AdminRevenueStatsResponse getRevenueStats(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();
        
        List<Object[]> revenueData = bookingRepository.findRevenueStatsByDateRange(fromDateTime, toDateTime);
        
        Double totalRevenue = 0.0;
        Double totalPlatformFee = 0.0;
        List<AdminRevenueStatsResponse.DailyRevenueStats> dailyStats = new ArrayList<>();
        
        Map<LocalDate, AdminRevenueStatsResponse.DailyRevenueStats> dailyStatsMap = 
            revenueData.stream()
                .collect(Collectors.toMap(
                    row -> {
                        if (row[0] instanceof Date) {
                            return ((Date) row[0]).toLocalDate();
                        } else if (row[0] instanceof java.sql.Timestamp) {
                            return ((java.sql.Timestamp) row[0]).toLocalDateTime().toLocalDate();
                        } else {
                            return ((LocalDateTime) row[0]).toLocalDate();
                        }
                    },
                    row -> {
                        LocalDate date;
                        if (row[0] instanceof Date) {
                            date = ((Date) row[0]).toLocalDate();
                        } else if (row[0] instanceof java.sql.Timestamp) {
                            date = ((java.sql.Timestamp) row[0]).toLocalDateTime().toLocalDate();
                        } else {
                            date = ((LocalDateTime) row[0]).toLocalDate();
                        }
                        return AdminRevenueStatsResponse.DailyRevenueStats.builder()
                            .date(date)
                            .revenue(((Number) row[1]).doubleValue())
                            .platformFee(((Number) row[2]).doubleValue())
                            .bookingCount(((Number) row[3]).longValue())
                            .build();
                    }
                ));
        
        // Fill in missing dates with zero values
        LocalDate currentDate = fromDate;
        while (!currentDate.isAfter(toDate)) {
            AdminRevenueStatsResponse.DailyRevenueStats dayStats = 
                dailyStatsMap.getOrDefault(currentDate, 
                    AdminRevenueStatsResponse.DailyRevenueStats.builder()
                        .date(currentDate)
                        .revenue(0.0)
                        .platformFee(0.0)
                        .bookingCount(0L)
                        .build());
            
            dailyStats.add(dayStats);
            totalRevenue += dayStats.getRevenue();
            totalPlatformFee += dayStats.getPlatformFee();
            
            currentDate = currentDate.plusDays(1);
        }

        return AdminRevenueStatsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalPlatformFee(totalPlatformFee)
                .fromDate(fromDate)
                .toDate(toDate)
                .dailyStats(dailyStats)
                .build();
    }

    @Override
    public AdminBookingTrendResponse getBookingsTrend(LocalDate fromDate, LocalDate toDate, String period) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();
        
        List<Object[]> trendData = bookingRepository.findBookingTrendByDateRange(fromDateTime, toDateTime);
        
        List<AdminBookingTrendResponse.BookingTrendData> trendList = new ArrayList<>();
        
        Map<LocalDate, Map<BookingStatus, Long>> trendMap = trendData.stream()
            .collect(Collectors.groupingBy(
                row -> {
                    if (row[0] instanceof Date) {
                        return ((Date) row[0]).toLocalDate();
                    } else if (row[0] instanceof java.sql.Timestamp) {
                        return ((java.sql.Timestamp) row[0]).toLocalDateTime().toLocalDate();
                    } else {
                        return ((LocalDateTime) row[0]).toLocalDate();
                    }
                },
                Collectors.toMap(
                    row -> BookingStatus.valueOf(row[1].toString()),
                    row -> ((Number) row[2]).longValue()
                )
            ));
        
        // Fill in missing dates and create trend data
        LocalDate currentDate = fromDate;
        while (!currentDate.isAfter(toDate)) {
            Map<BookingStatus, Long> dayData = trendMap.getOrDefault(currentDate, Map.of());
            
            AdminBookingTrendResponse.BookingTrendData trend = 
                AdminBookingTrendResponse.BookingTrendData.builder()
                    .date(currentDate)
                    .totalBookings(dayData.values().stream().mapToLong(Long::longValue).sum())
                    .completedBookings(dayData.getOrDefault(BookingStatus.COMPLETED, 0L))
                    .cancelledBookings(dayData.getOrDefault(BookingStatus.CANCELLED, 0L))
                    .pendingBookings(dayData.getOrDefault(BookingStatus.PENDING, 0L))
                    .build();
            
            trendList.add(trend);
            currentDate = currentDate.plusDays(1);
        }

        return AdminBookingTrendResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .period(period)
                .trendData(trendList)
                .build();
    }
}
