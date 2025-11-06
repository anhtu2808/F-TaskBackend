package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.booking.CancelBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.request.transaction.CreateTransactionRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.entity.*;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.enums.PaymentStatus;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.mapper.BookingMapper;
import com.anhtu.ftaskbackend.repository.*;
import com.anhtu.ftaskbackend.repository.specification.BookingSpecification;
import com.anhtu.ftaskbackend.service.BookingService;
import com.anhtu.ftaskbackend.service.TransactionService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        double platformFeePercent = variant.getServiceCatalog().getPlatformFeePercent() / 100;
        double variantPrice = variant.getPricePerVariant();
        Booking booking = Booking.builder()
                .customer(customer)
                .address(address)
                .variant(variant)
                .totalPrice(variant.getPricePerVariant())
                .requiredPartners(variant.getNumberOfPartners())
                .platformFee(variantPrice * platformFeePercent)
                .startAt(request.getStartAt())
                .completedAt(request.getStartAt().plusHours(variant.getDurationHours()))
                .customerNote(request.getCustomerNote())
                .build();
        bookingRepository.save(booking);
        paymentRepository.save(Payment.builder()
                .amount(booking.getTotalPrice())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .booking(booking)
                .build());
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
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelReason(request.getReason());
        bookingRepository.save(booking);
        if(!booking.getStartAt().isBefore(LocalDateTime.now().plusHours(4))){
            transactionService.createTransaction(CreateTransactionRequest.builder()
                    .type(TransactionType.FINE)
                    .amount(booking.getTotalPrice() * 0.3)
                    .build());
        }
    }
}
