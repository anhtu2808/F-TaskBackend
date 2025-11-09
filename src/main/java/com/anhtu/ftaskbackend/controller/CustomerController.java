package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.response.address.AddressResponse;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.dto.response.transaction.TransactionResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.AddressService;
import com.anhtu.ftaskbackend.service.BookingService;
import com.anhtu.ftaskbackend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerController {
    AddressService addressService;
    TransactionService transactionService;
    BookingService bookingService;

    @GetMapping("/addresses")
    public ApiResponse<List<AddressResponse>> getAllByCustomer() {
        Long customerId = JWTHelper.getCurrentCustomerId();
        List<AddressResponse> responses = addressService.getAllByCurrentUser(customerId);
        return ApiResponse.<List<AddressResponse>>builder()
                .result(responses)
                .build();
    }

}
