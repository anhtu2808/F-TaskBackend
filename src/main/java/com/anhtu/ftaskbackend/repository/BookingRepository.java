package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
    SELECT b
    FROM Booking b
    WHERE b.status = :status
      AND b.startAt BETWEEN :fromDate AND :toDate
    """)
    Page<Booking> findByStatusAndStartAtBetween(@Param("status")BookingStatus status,
                                                @Param("fromDate")LocalDateTime fromDate,
                                                @Param("toDate")LocalDateTime toDate,
                                                Pageable pageable);

}
