package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.response.address.AddressResponse;
import com.anhtu.ftaskbackend.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerController {
    AddressService addressService;

    @GetMapping("/{customerId}/address")
    public ApiResponse<List<AddressResponse>> getAllByCustomer(@PathVariable Long customerId) {
        List<AddressResponse> responses = addressService.getAllByCurrentUser(customerId);
        return ApiResponse.<List<AddressResponse>>builder()
                .result(responses)
                .build();
    }
}
