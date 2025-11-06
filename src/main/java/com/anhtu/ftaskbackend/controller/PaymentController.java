package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.dto.response.payment.PaymentResponse;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.BookingService;
import com.anhtu.ftaskbackend.service.PaymentService;
import com.anhtu.ftaskbackend.thirdParty.VNPayAPI;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/payments")
@FieldDefaults(level = PRIVATE)
public class PaymentController {

    @Autowired
    PaymentService paymentService;
    @Autowired
    VNPayAPI vnPayAPI;
    @Autowired
    BookingService bookingService;

    @PostMapping("/pay-for-booking")
    public ApiResponse<Map<String, Object>> payForBooking(@RequestParam Long bookingId) {
        BookingResponse bookingResponse = bookingService.getBookingById(bookingId);
        String info = "User_" + JWTHelper.getCurrentUserId() + "_type_PAYMENT_amount_" + bookingResponse.getTotalPrice() + "_booking_" + bookingId;
        return ApiResponse.<Map<String, Object>>builder()
                .message("Withdrawal wallet successful")
                .result(vnPayAPI.createPayment(bookingResponse.getTotalPrice(), info, TransactionType.ADJUSTMENT.toString()))
                .build();
    }

    @GetMapping("/vnpay-return")
    public ApiResponse<Void> vnpayReturn(@RequestParam(name = "vnp_OrderInfo") String vnp_OrderInfo,
                                         @RequestParam(name = "vnp_ResponseCode") String vnp_ResponseCode,
                                         @RequestParam(name = "vnp_TransactionStatus") String vnp_TransactionStatus) {
        String orderInfo = URLDecoder.decode(vnp_OrderInfo, StandardCharsets.UTF_8);
        paymentService.confirmPayment(orderInfo, vnp_ResponseCode, vnp_TransactionStatus);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Confirm payment successfully")
                .build();
    }


}
