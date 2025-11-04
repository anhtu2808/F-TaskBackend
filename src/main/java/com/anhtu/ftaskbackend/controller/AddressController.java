package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.address.AddressRequest;
import com.anhtu.ftaskbackend.dto.response.address.AddressResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
    @Operation(summary = "Create new address for current user")
    public ApiResponse<AddressResponse> create(@Valid @RequestBody AddressRequest request) {
        Long userId = JWTHelper.getCurrentUserId();
        AddressResponse response = addressService.create(userId, request);
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Address created successfully")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address by id")
    public ApiResponse<AddressResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        Long userId = JWTHelper.getCurrentUserId();
        AddressResponse response = addressService.update(userId, id, request);
        return ApiResponse.<AddressResponse>builder()
                .message("Address updated successfully")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by id")
    public ApiResponse<AddressResponse> getById(@PathVariable Long id) {
        Long userId = JWTHelper.getCurrentUserId();
        AddressResponse response = addressService.getById(userId, id);
        return ApiResponse.<AddressResponse>builder()
                .message("Get address successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address by id")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Long userId = JWTHelper.getCurrentUserId();
        addressService.delete(userId, id);
        return ApiResponse.<Void>builder()
                .message("Address deleted successfully")
                .build();
    }
}
