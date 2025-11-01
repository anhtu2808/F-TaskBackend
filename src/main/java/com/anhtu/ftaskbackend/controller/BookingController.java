package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.service.BookingService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    public ApiResponse<Page<BookingResponse>> createBooking(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "3") int size,
                                                            @RequestParam BookingStatus status,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate){
        return ApiResponse.<Page<BookingResponse>>builder()
                .code(200)
                .message("Get bookings successfully")
                .result(bookingService.getAllBookings(page - 1, size, status, fromDate, toDate))
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

}
