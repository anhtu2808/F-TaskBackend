package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.repository.specification.BookingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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


    @Query("""
                SELECT DISTINCT b
                FROM Booking b
                JOIN b.partners p
                WHERE b.status = 'COMPLETED'
                  AND p.status <> 'EARNED'
            """)
    List<Booking> findCompletedBookingsNotYetTransferred();
}
