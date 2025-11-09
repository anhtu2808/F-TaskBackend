package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    List<Booking> findBookingByStatus(BookingStatus status);

    Page<Booking> findBookingByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.status IN :statuses " +
           "AND b.startAt BETWEEN :now AND :sixHoursFromNow " +
           "AND b.insufficientPartnersNotificationSent = false")
    List<Booking> findBookingsNeedingInsufficientPartnersCheck(
            @Param("statuses") List<BookingStatus> statuses,
            @Param("now") LocalDateTime now,
            @Param("sixHoursFromNow") LocalDateTime sixHoursFromNow
    );

    @Query("SELECT b FROM Booking b WHERE b.insufficientPartnersNotificationSentAt IS NOT NULL " +
           "AND b.insufficientPartnersNotificationSentAt <= :oneHourAgo " +
           "AND b.status IN :statuses")
    List<Booking> findBookingsForAutoCancel(
            @Param("oneHourAgo") LocalDateTime oneHourAgo,
            @Param("statuses") List<BookingStatus> statuses
    );

    @Query("""
                SELECT DISTINCT b
                FROM Booking b
                JOIN b.partners p
                WHERE b.status = 'COMPLETED'
                  AND p.status <> 'EARNED'
            """)
    List<Booking> findCompletedBookingsNotYetTransferred();

    Long countByStatus(BookingStatus status);

    List<Booking> findByStatus(BookingStatus status);

    @Query(value = "SELECT DATE(b.created_at) as date, SUM(b.total_price) as revenue, SUM(b.platform_fee) as platformFee, COUNT(b.id) as bookingCount " +
           "FROM booking b WHERE b.status = 'COMPLETED' AND b.created_at BETWEEN :fromDate AND :toDate " +
           "GROUP BY DATE(b.created_at) ORDER BY DATE(b.created_at)", nativeQuery = true)
    List<Object[]> findRevenueStatsByDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query(value = "SELECT DATE(b.created_at) as date, b.status, COUNT(b.id) as count " +
           "FROM booking b WHERE b.created_at BETWEEN :fromDate AND :toDate " +
           "GROUP BY DATE(b.created_at), b.status ORDER BY DATE(b.created_at)", nativeQuery = true)
    List<Object[]> findBookingTrendByDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}
