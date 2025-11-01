package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBookingParams;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface BookingService {

    BookingResponse createBooking(CreateBookingRequest createBookingRequest);
    Page<BookingResponse> getAllBookings(FilterBookingParams filters);
    BookingResponse getBookingById(Long id);

}
