package com.anhtu.ftaskbackend.task;

import com.anhtu.ftaskbackend.dto.request.Wallet.AdjustWalletBalanceRequest;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.BookingPartner;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.repository.BookingPartnerRepository;
import com.anhtu.ftaskbackend.repository.BookingRepository;
import com.anhtu.ftaskbackend.repository.UserRepository;
import com.anhtu.ftaskbackend.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransferFundsForCompleteBookingTask {

    private final BookingRepository bookingRepository;
    private final BookingPartnerRepository bookingPartnerRepository;
    private final WalletService walletService;

    @Scheduled(fixedRate = 5000)
    public void transferFundsForCompleteBooking() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findBookingByStatus(BookingStatus.COMPLETED);
        if (!bookings.isEmpty()) {
            for (Booking booking : bookings) {
                if (now.isAfter(booking.getCompletedAt().plusHours(1))) {
                    List<BookingPartner> partners = bookingPartnerRepository.findByBooking_Id(booking.getId());
                    for (BookingPartner partner : partners) {
                        User user = partner.getPartner().getUser();
                        walletService.adjustBalance(user.getId(), AdjustWalletBalanceRequest.builder()
                                        .bookingId(booking.getId())
                                        .bookingPartnerId(partner.getId())
                                        .amount(partner.getPartnerEarnings())
                                        .type(TransactionType.EARNING)
                                .build());
                    }
                }
            }
        }
    }
}
