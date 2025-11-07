package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.BookingPartner;
import com.anhtu.ftaskbackend.entity.Partner;
import com.anhtu.ftaskbackend.enums.BookingPartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
