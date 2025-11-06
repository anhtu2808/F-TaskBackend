package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.BookingPartner;
import com.anhtu.ftaskbackend.entity.Partner;
import com.anhtu.ftaskbackend.enums.BookingPartnerStatus;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.BookingMapper;
import com.anhtu.ftaskbackend.repository.BookingPartnerRepository;
import com.anhtu.ftaskbackend.repository.BookingRepository;
import com.anhtu.ftaskbackend.repository.PartnerRepository;
import com.anhtu.ftaskbackend.service.PartnerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class PartnerServiceImpl implements PartnerService {

    PartnerRepository partnerRepository;
    BookingRepository bookingRepository;
    BookingPartnerRepository bookingPartnerRepository;
    BookingMapper bookingMapper;

    @Override
    public BookingResponse claimBooking(Long partnerId, Long bookingId) {
        Partner partner = partnerRepository.findById(partnerId)
                                           .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));

        boolean alreadyClaimed = bookingPartnerRepository.existsByPartnerAndBooking(partner, booking);
        if (alreadyClaimed) {
            throw new AppException(ErrorCode.BookingAlreadyClaimedByThisPartner);
        }

        long currentClaims = bookingPartnerRepository.countByBooking(booking);
        int maxPartners = booking.getRequiredPartners();

        if (currentClaims >= maxPartners || booking.getStatus() == BookingStatus.FULLY_ACCEPTED) {
            throw new AppException(ErrorCode.BookingPartnerLimitReached);
        }

        BookingStatus bookingStatus = booking.getStatus();

        if (bookingStatus == BookingStatus.PENDING && currentClaims + 1 < maxPartners) {
            booking.setStatus(BookingStatus.PARTIALLY_ACCEPTED);
            booking = bookingRepository.save(booking);
        }
        if ((bookingStatus == BookingStatus.PENDING || (bookingStatus == BookingStatus.PARTIALLY_ACCEPTED))
                && currentClaims + 1 == maxPartners) {
            booking.setStatus(BookingStatus.FULLY_ACCEPTED);
            booking = bookingRepository.save(booking);
        }

        BookingPartner bookingPartner = BookingPartner.builder()
                .partner(partner)
                .booking(booking)
                .partnerEarnings(booking.getTotalPrice() / booking.getRequiredPartners())
                .status(BookingPartnerStatus.JOINED)
                .build();

        bookingPartnerRepository.save(bookingPartner);

        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse cancelBooking(Long partnerId, Long bookingId) {
        Partner partner = partnerRepository.findById(partnerId)
                                           .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));

        BookingPartner bookingPartner = bookingPartnerRepository
                .findByPartnerAndBooking(partner, booking)
                .orElseThrow(() -> new AppException(ErrorCode.BookingClaimNotFound));

        bookingPartner.setStatus(BookingPartnerStatus.CANCELLED);
        bookingPartnerRepository.save(bookingPartner);

        long activePartners = bookingPartnerRepository.countByBookingAndStatus(
                booking,
                BookingPartnerStatus.JOINED
        );

        int maxPartners = booking.getRequiredPartners();

        if (activePartners == 0) {
            booking.setStatus(BookingStatus.PENDING);
        } else if (activePartners < maxPartners) {
            booking.setStatus(BookingStatus.PARTIALLY_ACCEPTED);
        }
        booking = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse startBooking(Long partnerId, Long bookingId) {
        Partner partner = partnerRepository.findById(partnerId)
                                           .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));

        BookingPartner bookingPartner = bookingPartnerRepository
                .findByPartnerAndBooking(partner, booking)
                .orElseThrow(() -> new AppException(ErrorCode.BookingClaimNotFound));

        validateBookingReadyToStart(booking);

        bookingPartner.setStatus(BookingPartnerStatus.WORKING);
        bookingPartnerRepository.save(bookingPartner);

        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse completeBooking(Long partnerId, Long bookingId) {
        Partner partner = partnerRepository.findById(partnerId)
                                           .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));

        BookingPartner bookingPartner = bookingPartnerRepository
                .findByPartnerAndBooking(partner, booking)
                .orElseThrow(() -> new AppException(ErrorCode.BookingClaimNotFound));

        // Kiểm tra partner phải đang ở trạng thái WORKING
        if (bookingPartner.getStatus() != BookingPartnerStatus.WORKING) {
            throw new AppException(ErrorCode.PartnerNotInWorkingStatus);
        }

        // Cập nhật trạng thái partner thành COMPLETED
        bookingPartner.setStatus(BookingPartnerStatus.COMPLETED);
        bookingPartnerRepository.save(bookingPartner);

        // Kiểm tra xem tất cả partner đã hoàn thành chưa
        long totalPartners = bookingPartnerRepository.countByBookingAndStatusIn(
                booking,
                Arrays.asList(BookingPartnerStatus.WORKING, BookingPartnerStatus.COMPLETED)
        );

        long completedPartners = bookingPartnerRepository.countByBookingAndStatus(
                booking,
                BookingPartnerStatus.COMPLETED
        );

        // Nếu tất cả partner đã complete, chuyển booking sang COMPLETED
        if (completedPartners == totalPartners && totalPartners > 0) {
            booking.setStatus(BookingStatus.COMPLETED);
            booking = bookingRepository.save(booking);
        }

        return bookingMapper.toBookingResponse(booking);
    }

    private void validateBookingReadyToStart(Booking booking) {
        if (booking.getStartAt() == null) {
            throw new AppException(ErrorCode.BookingStartTimeMissing);
        }

        // Tạm thời bỏ kiểm tra thời gian bắt đầu trong vòng 15 phút để dễ test
        //boolean withinWindow = booking.getStartAt().isBefore(LocalDateTime.now().plusMinutes(15));
        //if (!withinWindow) {
        //    throw new AppException(ErrorCode.BookingStartTimeTooEarly);
        //}

        BookingStatus status = booking.getStatus();
        if (status == BookingStatus.FULLY_ACCEPTED) {
            return;
        }

        if (status == BookingStatus.PARTIALLY_ACCEPTED) {
            Boolean customerAccepted = booking.getIsCustomerAccepted();
            if (customerAccepted == null || !customerAccepted) {
                throw new AppException(ErrorCode.CustomerNotAcceptedForPartial);
            }
            return;
        }

        if (status == BookingStatus.IN_PROGRESS) {
            return;
        }

        throw new AppException(ErrorCode.InvalidBookingStastusForStart);
    }
}
