package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.booking.ScanQRCodeRequest;
import com.anhtu.ftaskbackend.dto.request.partner.RegisterDistrictsRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.dto.response.district.DistrictResponse;
import com.anhtu.ftaskbackend.dto.response.review.ReviewResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.PartnerService;
import com.anhtu.ftaskbackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "Start booking", description = "Partner starts working on a claimed booking")
    public ApiResponse<BookingResponse> startBooking(@PathVariable Long bookingId) {
        Long partnerId = JWTHelper.getCurrentPartnerId();
        BookingResponse response = partnerService.startBooking(partnerId, bookingId);
        return ApiResponse.<BookingResponse>builder()
                .result(response)
                .message("Booking started successfully")
                .build();
    }

    @PostMapping("/bookings/start-by-qr")
    @Operation(summary = "Start booking by QR code", description = "Partner scans QR code and starts booking. QR token must be valid and not expired. Partner must have claimed the booking.")
    public ApiResponse<BookingResponse> startBookingByQR(@RequestBody ScanQRCodeRequest request) {
        BookingResponse response = partnerService.startBookingByQR(request.getQrToken());
        return ApiResponse.<BookingResponse>builder()
                .result(response)
                .message("Booking started successfully via QR code")
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

    @GetMapping("/my-reviews")
    @Operation(summary = "Get all reviews of current partner")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ReviewResponse>> getMyReviews() {
        Long partnerId = JWTHelper.getCurrentPartnerId();
        List<ReviewResponse> response = reviewService.getReviewsByPartner(partnerId);
        return ApiResponse.<List<ReviewResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get partner reviews successfully")
                .result(response)
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

    @PutMapping("/districts")
    @Operation(summary = "Update registered districts for partner", description = "Replace all registered districts with new list")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> registerDistricts(@Valid @RequestBody RegisterDistrictsRequest request) {
        Long partnerId = JWTHelper.getCurrentPartnerId();
        partnerService.registerDistricts(partnerId, request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Districts registered successfully")
                .build();
    }

    @GetMapping("/districts")
    @Operation(summary = "Get registered districts for current partner")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<DistrictResponse>> getRegisteredDistricts() {
        Long partnerId = JWTHelper.getCurrentPartnerId();
        List<DistrictResponse> response = partnerService.getRegisteredDistricts(partnerId);
        return ApiResponse.<List<DistrictResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get registered districts successfully")
                .result(response)
                .build();
    }
}
