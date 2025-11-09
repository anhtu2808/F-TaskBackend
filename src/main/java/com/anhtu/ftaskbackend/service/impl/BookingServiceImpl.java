package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.Wallet.AdjustWalletBalanceRequest;
import com.anhtu.ftaskbackend.dto.request.booking.CancelBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.request.booking.InsufficientPartnersResponseRequest;
import com.anhtu.ftaskbackend.dto.request.transaction.CreateTransactionRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingPartnerResponse;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.dto.response.booking.GenerateQRCodeResponse;
import com.anhtu.ftaskbackend.entity.*;
import com.anhtu.ftaskbackend.enums.*;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.helper.QRTokenHelper;
import com.anhtu.ftaskbackend.mapper.BookingMapper;
import com.anhtu.ftaskbackend.repository.*;
import com.anhtu.ftaskbackend.repository.specification.BookingSpecification;
import com.anhtu.ftaskbackend.service.BookingService;
import com.anhtu.ftaskbackend.service.TransactionService;
import com.anhtu.ftaskbackend.service.NotificationService;
import com.anhtu.ftaskbackend.service.WalletService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ServiceCatalogVariantRepository variantRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    BookingMapper bookingMapper;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    TransactionService transactionService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    BookingPartnerRepository bookingPartnerRepository;
    @Autowired
    WalletService walletService;
    @Autowired
    QRTokenHelper qrTokenHelper;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        Long userId = JWTHelper.getCurrentUserId();
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound));
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.AddressNotFound));
        ServiceCatalogVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.ServiceVariantNotFound));
        if (request.getStartAt().isBefore(LocalDateTime.now()))
            throw new AppException(ErrorCode.BookingStartAtInvalid);
        BookingStatus status = BookingStatus.PENDING;
        User user = customer.getUser();
        Wallet wallet = user.getWallet();
        if (request.getMethod().equals(PaymentMethod.CASH) || wallet.getBalance() < variant.getPricePerVariant()) {
            status = BookingStatus.WAITING_FOR_PAYMENT;
        }
        double platformFeePercent = variant.getServiceCatalog().getPlatformFeePercent() / 100;
        double variantPrice = variant.getPricePerVariant();
        Booking booking = Booking.builder()
                .customer(customer)
                .address(address)
                .variant(variant)
                .totalPrice(variant.getPricePerVariant())
                .requiredPartners(variant.getNumberOfPartners())
                .platformFee(variantPrice * platformFeePercent)
                .status(status)
                .startAt(request.getStartAt())
                .completedAt(request.getStartAt().plusHours(variant.getDurationHours()))
                .customerNote(request.getCustomerNote())
                .build();
        bookingRepository.save(booking);

        // Send confirmation notification to customer when booking is created
        notificationService.sendBookingPlacedConfirmationNotification(booking);

        Payment payment = Payment.builder()
                .amount(booking.getTotalPrice())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .booking(booking)
                .build();
        if (booking.getStatus().equals(BookingStatus.PENDING)) {
            walletService.adjustBalance(userId, AdjustWalletBalanceRequest.builder()
                    .amount(booking.getTotalPrice())
                    .type(TransactionType.ADJUSTMENT)
                    .bookingId(booking.getId())
                    .build());
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            // Send notification to eligible partners when booking is created and paid
            notificationService.sendBookingCreatedNotification(booking);
        } else {
            paymentRepository.save(payment);
        }
        BookingResponse response = bookingMapper.toBookingResponse(booking);
        response.setMethod(request.getMethod());
        return response;
    }

    @Override
    public Page<BookingResponse> getAllBookings(FilterBooking params) {
        var spec = BookingSpecification.filter(params);
        var pageable = PageRequest.of(params.getPage() - 1, params.getSize());
        return bookingRepository.findAll(spec, pageable)
                .map(bookingMapper::toBookingResponse);
    }

    @Override
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));
        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public void cancelBooking(Long id, CancelBookingRequest request) {
        Long currentUserId = JWTHelper.getCurrentUserId();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));

        // Idempotency: if already cancelled or completed, no-op
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            return;
        }

        // Ownership guard: only booking owner (customer) can cancel
        Long ownerUserId = booking.getCustomer().getUser().getId();
        if (!ownerUserId.equals(currentUserId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Disallow cancel if any partner has started working
        long workingPartners = bookingPartnerRepository.countByBookingAndStatus(booking, BookingPartnerStatus.WORKING);
        if (workingPartners > 0) {
            throw new AppException(ErrorCode.BadRequest);
        }

        // Compute penalty: 30% if within 6 hours to start
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilStart = Duration.between(now, booking.getStartAt()).toHours();
        boolean isInLast6Hours = hoursUntilStart < 6;
        double penalty = isInLast6Hours ? booking.getTotalPrice() * 0.30 : 0.0;

        // Determine claimed partners to split penalty
        List<BookingPartnerStatus> eligibleStatuses = List.of(BookingPartnerStatus.JOINED, BookingPartnerStatus.WORKING);
        List<BookingPartner> claimedPartners = bookingPartnerRepository.findByBookingAndStatusIn(booking, eligibleStatuses);
        int partnerCount = claimedPartners.size();
        double perPartnerShare = partnerCount > 0 ? penalty / partnerCount : 0.0;

        // Notifications
        notificationService.sendBookingCancelledNotification(booking, request.getReason());

        // Persist cancellation
        if (isInLast6Hours) {
            walletService.adjustBalance(ownerUserId, AdjustWalletBalanceRequest.builder()
                            .bookingId(booking.getId())
                            .type(TransactionType.FINE)
                            .amount(booking.getTotalPrice())
                    .build());
        }
        if (perPartnerShare > 0.0) {
            List<BookingPartner> partners = bookingPartnerRepository.findByBooking_Id(booking.getId());
            for (BookingPartner partner : partners) {
                User partnerUser = partner.getPartner().getUser();
                walletService.adjustBalance(partnerUser.getId(), AdjustWalletBalanceRequest.builder()
                                .type(TransactionType.REFUND)
                                .amount(perPartnerShare)
                                .bookingPartnerId(partner.getId())
                                .bookingId(booking.getId())
                        .build());
            }
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelReason(request.getReason());
        bookingRepository.save(booking);

        // TODO: Transactions integration points
        // - Deduct 'penalty' from customer wallet
        // - Credit 'perPartnerShare' to each partner's wallet
        // - Optional: refund remaining amount to customer if pre-paid policy applies

        // Example placeholders (disabled):
        // transactionService.createTransaction(CreateTransactionRequest.builder()
        //         .type(TransactionType.FINE)
        //         .amount(penalty)
        //         .description("Penalty for late cancellation (<4h)")
        //         .build());
        // claimedPartners.forEach(bp -> transactionService.createTransaction(CreateTransactionRequest.builder()
        //         .type(TransactionType.EARNING)
        //         .amount(perPartnerShare)
        //         .bookingPartnerId(bp.getId())
        //         .description("Share from cancellation penalty")
        //         .build()));
    }

    @Override
    public Page<BookingResponse> getAllCustomerBookings(Long customerId, FilterBooking params) {
        var spec = BookingSpecification.filter(params);
        var pageable = PageRequest.of(params.getPage() - 1, params.getSize());
        return bookingRepository.findBookingByCustomerId(customerId, pageable)
                .map(bookingMapper::toBookingResponse);
    }

    @Override
    public void handleInsufficientPartnersResponse(Long bookingId, InsufficientPartnersResponseRequest request) {
        Long currentUserId = JWTHelper.getCurrentUserId();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));

        // Ownership guard: only booking owner (customer) can respond
        Long ownerUserId = booking.getCustomer().getUser().getId();
        if (!ownerUserId.equals(currentUserId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Validate booking is in valid state
        if (booking.getStatus() == BookingStatus.CANCELLED || 
            booking.getStatus() == BookingStatus.COMPLETED ||
            booking.getStatus() == BookingStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.BadRequest);
        }

        // Disallow if any partner has started working
        long workingPartners = bookingPartnerRepository.countByBookingAndStatus(booking, BookingPartnerStatus.WORKING);
        if (workingPartners > 0) {
            throw new AppException(ErrorCode.BadRequest);
        }

        if (request.getCancel() != null && request.getCancel()) {
            // Customer chooses to cancel - full refund (no penalty)
            handleInsufficientPartnersCancellation(booking, ownerUserId);
        } else {
            // Customer chooses to continue - update partner earnings and proceed
            handleInsufficientPartnersContinue(booking);
        }
        
        // Clear the timestamp since customer has responded (prevents auto-cancel)
        booking.setInsufficientPartnersNotificationSentAt(null);
        bookingRepository.save(booking);
    }

    private void handleInsufficientPartnersCancellation(Booking booking, Long ownerUserId) {
        // Full refund to customer (no penalty)
        walletService.adjustBalance(ownerUserId, AdjustWalletBalanceRequest.builder()
                .bookingId(booking.getId())
                .type(TransactionType.REFUND)
                .amount(booking.getTotalPrice())
                .build());

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelReason("Khách hàng hủy do không đủ đối tác");
        // Clear timestamp is handled in the caller method
        bookingRepository.save(booking);

        // Send cancellation notification (will notify customer and any claimed partners)
        notificationService.sendBookingCancelledNotification(booking, "Khách hàng hủy do không đủ đối tác");
    }

    private void handleInsufficientPartnersContinue(Booking booking) {
        // Count current active partners
        long currentActivePartners = bookingPartnerRepository.countByBookingAndStatusIn(
                booking,
                List.of(BookingPartnerStatus.JOINED, BookingPartnerStatus.WORKING)
        );

        if (currentActivePartners == 0) {
            throw new AppException(ErrorCode.BadRequest);
        }

        // Recalculate partner earnings based on current number of partners
        double newEarningsPerPartner = booking.getTotalPrice() / currentActivePartners;

        // Update all active partners' earnings
        List<BookingPartner> activePartners = bookingPartnerRepository.findByBookingAndStatusIn(
                booking,
                List.of(BookingPartnerStatus.JOINED, BookingPartnerStatus.WORKING)
        );

        for (BookingPartner bookingPartner : activePartners) {
            bookingPartner.setPartnerEarnings(newEarningsPerPartner);
            bookingPartnerRepository.save(bookingPartner);
        }

        // Mark customer as accepted to continue
        booking.setIsCustomerAccepted(true);

        // Update booking status based on current partners
        if (currentActivePartners < booking.getRequiredPartners()) {
            booking.setStatus(BookingStatus.PARTIALLY_ACCEPTED);
        } else {
            booking.setStatus(BookingStatus.FULLY_ACCEPTED);
        }

        bookingRepository.save(booking);

        // Send confirmation notification
        notificationService.sendBookingPlacedConfirmationNotification(booking);
    }

    @Override
    public void autoCancelInsufficientPartnersBooking(Booking booking) {
        // This method is called by the scheduled task to auto-cancel bookings
        // that haven't received a response within 1 hour
        
        Long ownerUserId = booking.getCustomer().getUser().getId();
        
        // Full refund to customer (no penalty) - same as manual cancellation
        walletService.adjustBalance(ownerUserId, AdjustWalletBalanceRequest.builder()
                .bookingId(booking.getId())
                .type(TransactionType.REFUND)
                .amount(booking.getTotalPrice())
                .build());

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelReason("Tự động hủy do khách hàng không phản hồi sau 1 giờ khi không đủ đối tác");
        
        // Clear the timestamp since booking is now cancelled
        booking.setInsufficientPartnersNotificationSentAt(null);
        bookingRepository.save(booking);

        // Send cancellation notification
        notificationService.sendBookingCancelledNotification(booking, 
                "Tự động hủy do khách hàng không phản hồi sau 1 giờ khi không đủ đối tác");
    }

    @Override
    public GenerateQRCodeResponse generateQRCode(Long bookingId) {
        // Get current customer from JWT
        Long currentCustomerId = JWTHelper.getCurrentCustomerId();
        
        // Find booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));
        
        // Validate customer owns the booking
        if (!booking.getCustomer().getId().equals(currentCustomerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        // Validate booking is in valid status for QR code generation
        BookingStatus status = booking.getStatus();
//        Tạm thời bỏ validate đi để test QR code
//        if (status == BookingStatus.FULLY_ACCEPTED) {
//            // Allow QR code generation
//        } else if (status == BookingStatus.PARTIALLY_ACCEPTED) {
//            // Check if customer has accepted
//            Boolean customerAccepted = booking.getIsCustomerAccepted();
//            if (customerAccepted == null || !customerAccepted) {
//                throw new AppException(ErrorCode.BookingQRNotAvailable);
//            }
//        } else {
//            // QR code not available for other statuses
//            throw new AppException(ErrorCode.BookingQRNotAvailable);
//        }
        
        // Generate QR token (expires in 1 hour)
        String qrToken = qrTokenHelper.generateQRToken(bookingId);
        
        return GenerateQRCodeResponse.builder()
                .qrToken(qrToken)
                .build();
    }
}
