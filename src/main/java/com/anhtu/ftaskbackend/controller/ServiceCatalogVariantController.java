package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.servicevariant.CreateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.response.ServiceVariant.ServiceVariantResponse;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import com.anhtu.ftaskbackend.service.ServiceVariantService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/service-variants")
@FieldDefaults(level = PRIVATE)
public class ServiceCatalogVariantController {

    @Autowired
    ServiceVariantService serviceVariantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ServiceVariantResponse> createServiceVariant(@RequestBody CreateServiceVariantRequest request) {
        return ApiResponse.<ServiceVariantResponse>builder()
                .code(201)
                .message("Create service catalog variant successfully")
                .result(serviceVariantService.createServiceVariant(request))
                .build();
    }

}
