package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.admin.AdminBookingFilterRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminBookingStatusUpdateRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminBookingRefundRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Admin Bookings", description = "Admin booking management APIs")
public class AdminBookingController {

    BookingService bookingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all bookings with advanced filters", description = "Get paginated list of bookings with comprehensive filtering options for admin")
    public ApiResponse<Page<BookingResponse>> getAllBookings(@ParameterObject AdminBookingFilterRequest filter) {
        return ApiResponse.<Page<BookingResponse>>builder()
                .code(200)
                .message("Bookings retrieved successfully")
                .result(bookingService.getAllBookingsForAdmin(filter))
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get booking details", description = "Get detailed information about a specific booking")
    public ApiResponse<BookingResponse> getBookingById(@PathVariable Long id) {
        return ApiResponse.<BookingResponse>builder()
                .code(200)
                .message("Booking details retrieved successfully")
                .result(bookingService.getBookingById(id))
                .build();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update booking status", description = "Admin can update booking status with optional reason")
    public ApiResponse<Void> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminBookingStatusUpdateRequest request) {
        bookingService.adminUpdateBookingStatus(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Booking status updated successfully")
                .build();
    }

    @PutMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Admin cancel booking", description = "Admin can cancel booking with full refund to customer")
    public ApiResponse<Void> cancelBooking(
            @PathVariable Long id,
            @Valid @RequestBody AdminBookingStatusUpdateRequest request) {
        bookingService.adminCancelBooking(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Booking cancelled successfully")
                .build();
    }

    @PostMapping("/{id}/refund")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Admin refund booking", description = "Admin can process partial or full refund for a booking")
    public ApiResponse<Void> refundBooking(
            @PathVariable Long id,
            @Valid @RequestBody AdminBookingRefundRequest request) {
        bookingService.adminRefundBooking(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Refund processed successfully")
                .build();
    }
}
