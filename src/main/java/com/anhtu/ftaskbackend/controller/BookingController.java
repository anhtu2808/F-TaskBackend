package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.booking.CancelBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.request.chat.ChatRequest;
import com.anhtu.ftaskbackend.dto.request.booking.InsufficientPartnersResponseRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.dto.response.chat.ChatResponse;
import com.anhtu.ftaskbackend.dto.response.booking.GenerateQRCodeResponse;
import com.anhtu.ftaskbackend.service.BookingService;
import com.anhtu.ftaskbackend.service.ChatService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;

@RestController
@RequestMapping("/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {

    @Autowired
    BookingService bookingService;
    @Autowired
    ChatService chatService;

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

    @GetMapping("/available/list")
    public ApiResponse<Page<BookingResponse>> getAvailableBookings(@ParameterObject FilterBooking params){
        return ApiResponse.<Page<BookingResponse>>builder()
                .code(200)
                .message("Get bookings successfully")
                .result(bookingService.getAllAvailableBookings(params))
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
    @Operation(summary = "Cancel booking (customer only)", description = "Cancel ≥ 4h before start: no penalty. Cancel < 4h: 30% penalty split among claimed partners. Disallowed if any partner is working.")
    public ApiResponse<Void> cancelBooking(@PathVariable Long id, @RequestBody CancelBookingRequest request){
        bookingService.cancelBooking(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Cancel booking successfully")
                .build();
    }

    @PostMapping("/{id}/insufficient-partners-response")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Handle insufficient partners response", description = "Customer can choose to cancel (full refund) or continue (cost split among available partners) when booking has insufficient partners within 6 hours of start time.")
    public ApiResponse<Void> handleInsufficientPartnersResponse(
            @PathVariable Long id,
            @RequestBody InsufficientPartnersResponseRequest request) {
        bookingService.handleInsufficientPartnersResponse(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message(request.getCancel() != null && request.getCancel()
                    ? "Booking cancelled successfully with full refund"
                    : "Booking will continue with available partners")
                .build();
    }

    @GetMapping("/{bookingId}/qr-code")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate QR code for booking", description = "Customer generates QR code token for booking. Token is valid for 1 hour. Only available for FULLY_ACCEPTED or PARTIALLY_ACCEPTED (with customer accepted) bookings.")
    public ApiResponse<GenerateQRCodeResponse> generateQRCode(@PathVariable Long bookingId) {
        GenerateQRCodeResponse response = bookingService.generateQRCode(bookingId);
        return ApiResponse.<GenerateQRCodeResponse>builder()
                .code(200)
                .message("QR code generated successfully")
                .result(response)
                .build();
    }

    @PostMapping("/{id}/chat")
    public ApiResponse<ChatResponse> CreateChatMessage(@PathVariable Long id, @RequestBody ChatRequest request){
        return ApiResponse.<ChatResponse>builder()
                .code(200)
                .message("Create chat message successfully")
                .result(chatService.sendMessage(id, request))
                .build();
    }

}
