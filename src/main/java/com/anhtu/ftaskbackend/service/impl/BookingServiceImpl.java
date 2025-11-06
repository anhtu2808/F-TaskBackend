package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.Wallet.AdjustWalletBalanceRequest;
import com.anhtu.ftaskbackend.dto.request.booking.CancelBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.request.transaction.CreateTransactionRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.entity.*;
import com.anhtu.ftaskbackend.enums.*;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.helper.JWTHelper;
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
        }
        paymentRepository.save(payment);
        return bookingMapper.toBookingResponse(booking);
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

        // Compute penalty: 30% if within 4 hours to start
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilStart = Duration.between(now, booking.getStartAt()).toHours();
        double penalty = hoursUntilStart < 4 ? booking.getTotalPrice() * 0.30 : 0.0;

        // Determine claimed partners to split penalty
        List<BookingPartnerStatus> eligibleStatuses = List.of(BookingPartnerStatus.JOINED, BookingPartnerStatus.WORKING);
        List<BookingPartner> claimedPartners = bookingPartnerRepository.findByBookingAndStatusIn(booking, eligibleStatuses);
        int partnerCount = claimedPartners.size();
        double perPartnerShare = partnerCount > 0 ? penalty / partnerCount : 0.0;

        // Persist cancellation
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelReason(request.getReason());
        bookingRepository.save(booking);
        if (!booking.getStartAt().isBefore(LocalDateTime.now().plusHours(4))) {
            transactionService.createTransaction(CreateTransactionRequest.builder()
                    .type(TransactionType.FINE)
                    .amount(booking.getTotalPrice() * 0.3)
                    .build());
        }

        // Notifications
        notificationService.sendBookingCancelledNotification(booking, request.getReason());

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
}
