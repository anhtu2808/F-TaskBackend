package com.anhtu.ftaskbackend.task;

import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.enums.BookingPartnerStatus;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.repository.BookingPartnerRepository;
import com.anhtu.ftaskbackend.repository.BookingRepository;
import com.anhtu.ftaskbackend.service.NotificationService;
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
public class InsufficientPartnersNotificationTask {

    private final BookingRepository bookingRepository;
    private final BookingPartnerRepository bookingPartnerRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60000) // Run every 1 minute (60000 ms)
    @Transactional
    public void checkAndNotifyInsufficientPartners() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixHoursFromNow = now.plusHours(6);

        List<BookingStatus> statusesToCheck = Arrays.asList(
                BookingStatus.PENDING,
                BookingStatus.PARTIALLY_ACCEPTED
        );

        List<Booking> bookingsToCheck = bookingRepository.findBookingsNeedingInsufficientPartnersCheck(
                statusesToCheck,
                now,
                sixHoursFromNow
        );

        log.info("Checking {} bookings for insufficient partners", bookingsToCheck.size());

        for (Booking booking : bookingsToCheck) {
            try {
                // Count current active partners (JOINED or WORKING)
                long currentPartners = bookingPartnerRepository.countByBookingAndStatusIn(
                        booking,
                        Arrays.asList(BookingPartnerStatus.JOINED, BookingPartnerStatus.WORKING)
                );

                int requiredPartners = booking.getRequiredPartners();

                // Only send notification if partners are insufficient
                if (currentPartners < requiredPartners) {
                    log.info("Booking {} has insufficient partners: {}/{}", 
                            booking.getId(), currentPartners, requiredPartners);

                    // Send notification
                    notificationService.sendInsufficientPartnersNotification(booking);

                    // Mark notification as sent and record timestamp
                    booking.setInsufficientPartnersNotificationSent(true);
                    booking.setInsufficientPartnersNotificationSentAt(LocalDateTime.now());
                    bookingRepository.save(booking);

                    log.info("Sent insufficient partners notification for booking {} at {}", 
                            booking.getId(), booking.getInsufficientPartnersNotificationSentAt());
                } else {
                    // If partners are now sufficient, mark notification as sent to avoid future checks
                    booking.setInsufficientPartnersNotificationSent(true);
                    bookingRepository.save(booking);
                }
            } catch (Exception e) {
                log.error("Error processing booking {} for insufficient partners check: {}", 
                        booking.getId(), e.getMessage(), e);
            }
        }
    }
}

