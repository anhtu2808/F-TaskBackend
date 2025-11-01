package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.entity.Address;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.Customer;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.mapper.BookingMapper;
import com.anhtu.ftaskbackend.repository.AddressRepository;
import com.anhtu.ftaskbackend.repository.BookingRepository;
import com.anhtu.ftaskbackend.repository.CustomerRepository;
import com.anhtu.ftaskbackend.repository.ServiceCatalogVariantRepository;
import com.anhtu.ftaskbackend.repository.specification.BookingSpecification;
import com.anhtu.ftaskbackend.service.BookingService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
