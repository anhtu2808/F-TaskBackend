package com.anhtu.ftaskbackend.task;

import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.repository.BookingRepository;
import com.anhtu.ftaskbackend.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoCancelInsufficientPartnersTask {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Scheduled(fixedRate = 60000) // Run every 1 minute (60000 ms)
    @Transactional
    public void autoCancelInsufficientPartnersBookings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);

        List<BookingStatus> statusesToCheck = Arrays.asList(
                BookingStatus.PENDING,
                BookingStatus.PARTIALLY_ACCEPTED
        );

        List<Booking> bookingsToCancel = bookingRepository.findBookingsForAutoCancel(
                oneHourAgo,
                statusesToCheck
        );

        log.info("Found {} bookings to auto-cancel due to no response after 1 hour", bookingsToCancel.size());

        for (Booking booking : bookingsToCancel) {
            try {
                // Double-check status to avoid duplicate cancellations
                if (booking.getStatus() == BookingStatus.CANCELLED || 
                    booking.getStatus() == BookingStatus.COMPLETED ||
                    booking.getStatus() == BookingStatus.IN_PROGRESS) {
                    log.info("Skipping booking {} - already cancelled, completed, or in progress", booking.getId());
                    continue;
                }

                log.info("Auto-cancelling booking {} - no response after 1 hour", booking.getId());
                bookingService.autoCancelInsufficientPartnersBooking(booking);
                log.info("Successfully auto-cancelled booking {}", booking.getId());
            } catch (Exception e) {
                log.error("Error auto-cancelling booking {}: {}", booking.getId(), e.getMessage(), e);
            }
        }
    }
}

