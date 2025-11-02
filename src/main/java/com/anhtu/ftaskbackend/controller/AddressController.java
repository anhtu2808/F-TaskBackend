package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.address.AddressRequest;
import com.anhtu.ftaskbackend.dto.response.address.AddressResponse;
import com.anhtu.ftaskbackend.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AddressController {

    AddressService addressService;

    @PostMapping
    public ApiResponse<AddressResponse> create(@RequestBody AddressRequest request) {
        AddressResponse response = addressService.create(request);
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Address created successfully")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(
            @PathVariable Long id,
            @RequestBody AddressRequest request) {
        AddressResponse response = addressService.update(id, request);
        return ApiResponse.<AddressResponse>builder()
                .message("Address updated successfully")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> getById(@PathVariable Long id) {
        AddressResponse response = addressService.getById(id);
        return ApiResponse.<AddressResponse>builder()
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Address deleted successfully")
                .build();
    }
}
