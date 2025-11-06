package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.dto.response.review.ReviewResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.PartnerService;
import com.anhtu.ftaskbackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/partners")
@Tag(name = "Partner", description = "APIs for Partner")
public class PartnerController {

    private final PartnerService partnerService;
    private final ReviewService reviewService;

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
    @Operation(summary = "Cancel booking claim (partner only)", description = "Partner cancels their claim. Cancel ≥ 4h before start: no penalty. Cancel < 4h: 30% penalty applied to partner. Disallowed if partner is already working.")
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

    @GetMapping("/{partnerId}/reviews")
    @Operation(summary = "Get all reviews of a partner")
    public ApiResponse<List<ReviewResponse>> getPartnerReviews(@PathVariable Long partnerId) {
        List<ReviewResponse> response = reviewService.getReviewsByPartner(partnerId);
        return ApiResponse.<List<ReviewResponse>>builder()
                .message("Get partner reviews successfully")
                .result(response)
                .build();
    }
}
