package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.servicevariant.CreateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.request.servicevariant.FilterServiceVariant;
import com.anhtu.ftaskbackend.dto.request.servicevariant.UpdateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.response.ServiceVariant.ServiceVariantResponse;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import com.anhtu.ftaskbackend.service.ServiceVariantService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<ServiceVariantResponse>> getAllServiceVariant(@ParameterObject FilterServiceVariant params) {
        return ApiResponse.<Page<ServiceVariantResponse>>builder()
                .code(200)
                .message("Get all service catalog variant successfully")
                .result(serviceVariantService.findAllServiceVariants(params))
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ServiceVariantResponse> getServiceVariantById(@PathVariable Long id) {
        return ApiResponse.<ServiceVariantResponse>builder()
                .code(200)
                .message("Get service catalog variant by id successfully")
                .result(serviceVariantService.findServiceVariantById(id))
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public ApiResponse<ServiceVariantResponse> updateServiceVariant(@PathVariable Long id, @RequestBody UpdateServiceVariantRequest request) {
        return ApiResponse.<ServiceVariantResponse>builder()
                .code(200)
                .message("Update service catalog variant successfully")
                .result(serviceVariantService.updateServiceVariant(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteServiceVariant(@PathVariable Long id) {
        serviceVariantService.deleteServiceVariant(id);
        return ApiResponse.<Void>builder()
                .code(204)
                .message("Delete service catalog variant successfully")
                .build();
    }


}
