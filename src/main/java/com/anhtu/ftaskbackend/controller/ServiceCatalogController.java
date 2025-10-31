package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;

import com.anhtu.ftaskbackend.dto.request.serviceccatalog.ServiceCatalogRequest;
import com.anhtu.ftaskbackend.dto.response.servicecatelog.ServiceCatalogResponse;
import com.anhtu.ftaskbackend.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/service-catalogs")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ServiceCatalogController {

    ServiceCatalogService service;

    @PostMapping
    public ApiResponse<ServiceCatalogResponse> create(@Valid @RequestBody ServiceCatalogRequest request) {
        return ApiResponse.<ServiceCatalogResponse>builder()
                .result(service.create(request))
                .message("Created successfully")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceCatalogResponse> update(
            @PathVariable Long id,
            @RequestBody ServiceCatalogRequest request
    ) {
        return ApiResponse.<ServiceCatalogResponse>builder()
                .result(service.update(id, request))
                .message("Updated successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceCatalogResponse> getById(@PathVariable Long id) {
        return ApiResponse.<ServiceCatalogResponse>builder()
                .result(service.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ServiceCatalogResponse>> getAll() {
        return ApiResponse.<List<ServiceCatalogResponse>>builder()
                .result(service.getAll())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.<Void>builder()
                .message("Deleted successfully")
                .build();
    }
}
