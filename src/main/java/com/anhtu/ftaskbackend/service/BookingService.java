package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(CreateBookingRequest createBookingRequest);

}
