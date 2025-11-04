package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.booking.CancelBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.service.BookingService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookingResponse> createBooking(@RequestBody CreateBookingRequest request){
        return ApiResponse.<BookingResponse>builder()
                .code(201)
                .message("Create booking successfully")
                .result(bookingService.createBooking(request))
                .build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<BookingResponse>> getBookings(@ParameterObject FilterBooking params){
        return ApiResponse.<Page<BookingResponse>>builder()
                .code(200)
                .message("Get bookings successfully")
                .result(bookingService.getAllBookings(params))
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BookingResponse> getBooking(@PathVariable Long id){
        return ApiResponse.<BookingResponse>builder()
                .code(200)
                .message("Get booking by id successfully")
                .result(bookingService.getBookingById(id))
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> cancelBooking(@PathVariable Long id, @RequestBody CancelBookingRequest request){
        bookingService.cancelBooking(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Cancel booking successfully")
                .build();
    }

}
