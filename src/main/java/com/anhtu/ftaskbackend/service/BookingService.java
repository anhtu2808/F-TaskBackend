package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.booking.CancelBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.CreateBookingRequest;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.request.booking.InsufficientPartnersResponseRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminBookingFilterRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminBookingStatusUpdateRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminBookingRefundRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.dto.response.booking.GenerateQRCodeResponse;
import com.anhtu.ftaskbackend.entity.Booking;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface BookingService {

    BookingResponse createBooking(CreateBookingRequest createBookingRequest);
    Page<BookingResponse> getAllBookings(FilterBooking params);
    BookingResponse getBookingById(Long id);
    void cancelBooking(Long id, CancelBookingRequest request);
    Page<BookingResponse> getAllCustomerBookings(Long customerId, FilterBooking params);
    void handleInsufficientPartnersResponse(Long bookingId, InsufficientPartnersResponseRequest request);
    GenerateQRCodeResponse generateQRCode(Long bookingId);
    void autoCancelInsufficientPartnersBooking(Booking booking);
    Page<BookingResponse> getAllAvailableBookings(FilterBooking params);
    // Admin methods
    Page<BookingResponse> getAllBookingsForAdmin(AdminBookingFilterRequest filter);
    void adminUpdateBookingStatus(Long bookingId, AdminBookingStatusUpdateRequest request);
    void adminCancelBooking(Long bookingId, AdminBookingStatusUpdateRequest request);
    void adminRefundBooking(Long bookingId, AdminBookingRefundRequest request);

}
