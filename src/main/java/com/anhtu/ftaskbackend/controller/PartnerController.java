package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.PartnerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/partners")
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping("/bookings/{bookingId}/claim")
    public ApiResponse<BookingResponse> claimBooking(@PathVariable Long bookingId) {
        Long partnerId = JWTHelper.getCurrentPartnerId();
        BookingResponse response = partnerService.claimBooking(partnerId, bookingId);
        return ApiResponse.<BookingResponse>builder()
                .result(response)
                .message("Claim booking successfully")
                .build();
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public ApiResponse<BookingResponse> cancelBooking(@PathVariable Long bookingId) {
        Long partnerId = JWTHelper.getCurrentPartnerId();
        BookingResponse response = partnerService.cancelBooking(partnerId, bookingId);
        return ApiResponse.<BookingResponse>builder()
                .result(response)
                .message("Booking cancelled successfully")
                .build();
    }

    @PostMapping("/bookings/{bookingId}/start")
    public ApiResponse<BookingResponse> startBooking(@PathVariable Long bookingId) {
        Long partnerId = JWTHelper.getCurrentPartnerId();
        BookingResponse response = partnerService.startBooking(partnerId, bookingId);
        return ApiResponse.<BookingResponse>builder()
                .result(response)
                .message("Booking started successfully")
                .build();
    }

    @PostMapping("/bookings/{bookingId}/complete")
    public ApiResponse<BookingResponse> completeBooking(@PathVariable Long bookingId) {
        Long partnerId = JWTHelper.getCurrentPartnerId();
        BookingResponse response = partnerService.completeBooking(partnerId, bookingId);
        return ApiResponse.<BookingResponse>builder()
                .result(response)
                .message("Booking completed successfully")
                .build();
    }
}
