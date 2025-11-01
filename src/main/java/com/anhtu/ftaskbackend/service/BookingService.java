package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import org.springframework.data.domain.Page;

public interface BookingService {

    BookingResponse createBooking(CreateBookingRequest createBookingRequest);
    Page<BookingResponse> getAllBookings(FilterBooking params);
    BookingResponse getBookingById(Long id);

}
