package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.BookingPartner;
import com.anhtu.ftaskbackend.entity.Partner;
import com.anhtu.ftaskbackend.enums.BookingPartnerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingPartnerRepository  extends JpaRepository<BookingPartner, Long> {
    boolean existsByPartnerAndBooking (Partner partner, Booking booking);
    long countByBooking (Booking booking);
    Optional<BookingPartner> findByPartnerAndBooking(Partner partner, Booking booking);
    long countByBookingAndStatus(Booking booking, BookingPartnerStatus status);
    long countByBookingAndStatusIn(Booking booking, List<BookingPartnerStatus> statuses);
    List<BookingPartner> findByBookingAndStatusIn(Booking booking, List<BookingPartnerStatus> statuses);
    List<BookingPartner> findByBooking_Id(Long id);
    Page<BookingPartner> findByPartner_Id(Long partnerId, Pageable pageable);

    @Query("""
        SELECT bp.booking.id
        FROM BookingPartner bp
        WHERE bp.partner.id = :partnerId
          AND bp.status = :status
    """)
    List<Long> findBookingIdsByPartnerIdAndStatus(
            @Param("partnerId") Long partnerId,
            @Param("status") BookingPartnerStatus status
    );

}
