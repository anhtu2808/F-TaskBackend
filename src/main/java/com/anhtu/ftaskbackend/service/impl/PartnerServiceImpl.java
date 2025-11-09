package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.partner.RegisterDistrictsRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerFilterRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerStatusUpdateRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerDistrictsRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.dto.response.district.DistrictResponse;
import com.anhtu.ftaskbackend.dto.response.partner.PartnerResponse;
import com.anhtu.ftaskbackend.dto.request.Wallet.AdjustWalletBalanceRequest;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.BookingPartner;
import com.anhtu.ftaskbackend.entity.District;
import com.anhtu.ftaskbackend.entity.Partner;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.enums.BookingPartnerStatus;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.helper.QRTokenHelper;
import com.anhtu.ftaskbackend.mapper.BookingMapper;
import com.anhtu.ftaskbackend.mapper.DistrictMapper;
import com.anhtu.ftaskbackend.mapper.PartnerMapper;
import com.anhtu.ftaskbackend.repository.BookingPartnerRepository;
import com.anhtu.ftaskbackend.repository.BookingRepository;
import com.anhtu.ftaskbackend.repository.DistrictRepository;
import com.anhtu.ftaskbackend.repository.PartnerRepository;
import com.anhtu.ftaskbackend.repository.specification.AdminPartnerSpecification;
import com.anhtu.ftaskbackend.service.NotificationService;
import com.anhtu.ftaskbackend.service.PartnerService;
import com.anhtu.ftaskbackend.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
    NotificationService notificationService;
    DistrictRepository districtRepository;
    DistrictMapper districtMapper;
    PartnerMapper partnerMapper;
    WalletService walletService;
    QRTokenHelper qrTokenHelper;

    @Override
    public BookingResponse claimBooking(Long partnerId, Long bookingId) {
        Partner partner = partnerRepository.findById(partnerId)
                                           .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));

        // Check if partner's wallet balance is negative
        User partnerUser = partner.getUser();
        if (partnerUser != null && partnerUser.getWallet() != null) {
            Double walletBalance = partnerUser.getWallet().getBalance();
            if (walletBalance != null && walletBalance < 0) {
                throw new AppException(ErrorCode.WalletNegativeBalance);
            }
        }

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

        // Send notification to customer when partner claims booking
        notificationService.sendBookingClaimedNotification(booking);

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

        // Idempotency: if already cancelled, no-op
        if (bookingPartner.getStatus() == BookingPartnerStatus.CANCELLED) {
            return bookingMapper.toBookingResponse(booking);
        }

        // Disallow cancel if partner has started working
        if (bookingPartner.getStatus() == BookingPartnerStatus.WORKING) {
            throw new AppException(ErrorCode.PartnerNotInWorkingStatus);
        }

        // Compute penalty: 30% of booking total if within 6 hours to start
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilStart = Duration.between(now, booking.getStartAt()).toHours();
        double penalty = hoursUntilStart < 6 ? booking.getTotalPrice() * 0.30 : 0.0;

        // Persist cancellation
        bookingPartner.setStatus(BookingPartnerStatus.CANCELLED);
        bookingPartnerRepository.save(bookingPartner);

        // Update booking status based on remaining active partners
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

        // Send notification to customer when partner cancels claim
        notificationService.sendPartnerCancelledClaimNotification(booking, partner);

        // Deduct penalty from partner's wallet if penalty > 0
        if (penalty > 0) {
            User partnerUser = partner.getUser();
            walletService.adjustBalance(partnerUser.getId(), AdjustWalletBalanceRequest.builder()
                    .type(TransactionType.FINE)
                    .amount(penalty)
                    .bookingPartnerId(bookingPartner.getId())
                    .bookingId(booking.getId())
                    .build());
        }

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

        // Send notification to customer when partner starts working
        notificationService.sendBookingStartedNotification(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse startBookingByQR(String qrToken) {
        // Verify QR token and extract bookingId
        Long bookingId = qrTokenHelper.verifyQRToken(qrToken);
        
        // Get current authenticated partner from JWT
        Long partnerId = JWTHelper.getCurrentPartnerId();
        
        // Reuse existing startBooking logic (includes all validations)
        return startBooking(partnerId, bookingId);
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
            // Send notification to customer when all partners complete the booking
            notificationService.sendBookingCompletedNotification(booking);
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

    @Override
    public void registerDistricts(Long partnerId, RegisterDistrictsRequest request) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        // Validate all district IDs exist
        List<District> districts = districtRepository.findAllById(request.getDistrictIds());
        
        if (districts.size() != request.getDistrictIds().size()) {
            throw new AppException(ErrorCode.BadRequest);
        }

        // Replace all existing districts with new ones
        partner.getDistricts().clear();
        partner.getDistricts().addAll(new HashSet<>(districts));
        
        partnerRepository.save(partner);
    }

    @Override
    public List<DistrictResponse> getRegisteredDistricts(Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        return partner.getDistricts().stream()
                .map(districtMapper::toDistrictResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DistrictResponse> getAllDistricts() {
        List<District> districts = districtRepository.findAll();
        return districtMapper.toDistrictResponseList(districts);
    }

    // Admin methods implementation
    @Override
    public Page<PartnerResponse> getAllPartnersForAdmin(AdminPartnerFilterRequest filter) {
        var spec = AdminPartnerSpecification.filter(filter);
        
        // Create sort
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(filter.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
            filter.getSortBy()
        );
        
        var pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        return partnerRepository.findAll(spec, pageable)
                .map(partnerMapper::toPartnerResponse);
    }

    @Override
    public PartnerResponse adminGetPartnerById(Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));
        return partnerMapper.toPartnerResponse(partner);
    }

    @Override
    public void adminUpdatePartnerStatus(Long partnerId, AdminPartnerStatusUpdateRequest request) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        partner.setIsAvailable(request.getIsAvailable());
        partnerRepository.save(partner);
    }

    @Override
    public void adminUpdatePartnerDistricts(Long partnerId, AdminPartnerDistrictsRequest request) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        // Get districts by IDs
        List<District> districts = districtRepository.findAllById(request.getDistrictIds());
        if (districts.size() != request.getDistrictIds().size()) {
            throw new AppException(ErrorCode.DistrictNotFound);
        }

        // Replace all existing districts with new ones
        partner.getDistricts().clear();
        partner.getDistricts().addAll(new HashSet<>(districts));
        
        partnerRepository.save(partner);
    }

    @Override
    public Page<BookingResponse> adminGetPartnerBookings(Long partnerId, Integer page, Integer size) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        
        // Find all bookings where this partner is involved
        Page<BookingPartner> bookingPartners = bookingPartnerRepository.findByPartner(partner, pageable);
        
        return bookingPartners.map(bp -> bookingMapper.toBookingResponse(bp.getBooking()));
    }
}
