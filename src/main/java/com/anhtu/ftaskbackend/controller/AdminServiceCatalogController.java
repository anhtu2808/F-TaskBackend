package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.admin.AdminServiceCatalogFilterRequest;
import com.anhtu.ftaskbackend.dto.request.serviceccatalog.ServiceCatalogRequest;
import com.anhtu.ftaskbackend.dto.response.servicecatelog.ServiceCatalogResponse;
import com.anhtu.ftaskbackend.service.ServiceCatalogService;
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
@RequestMapping("/admin/service-catalogs")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Admin Service Catalogs", description = "Admin service catalog management APIs")
public class AdminServiceCatalogController {

    ServiceCatalogService serviceCatalogService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all service catalogs with filters", description = "Get paginated list of service catalogs with comprehensive filtering options for admin")
    public ApiResponse<Page<ServiceCatalogResponse>> getAllServiceCatalogs(@ParameterObject AdminServiceCatalogFilterRequest filter) {
        return ApiResponse.<Page<ServiceCatalogResponse>>builder()
                .code(200)
                .message("Service catalogs retrieved successfully")
                .result(serviceCatalogService.getAllForAdmin(filter))
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get service catalog details", description = "Get detailed information about a specific service catalog")
    public ApiResponse<ServiceCatalogResponse> getServiceCatalogById(@PathVariable Long id) {
        return ApiResponse.<ServiceCatalogResponse>builder()
                .code(200)
                .message("Service catalog details retrieved successfully")
                .result(serviceCatalogService.getById(id))
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create service catalog", description = "Admin can create new service catalog")
    public ApiResponse<ServiceCatalogResponse> createServiceCatalog(@Valid @RequestBody ServiceCatalogRequest request) {
        return ApiResponse.<ServiceCatalogResponse>builder()
                .code(201)
                .message("Service catalog created successfully")
                .result(serviceCatalogService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update service catalog", description = "Admin can update service catalog information")
    public ApiResponse<ServiceCatalogResponse> updateServiceCatalog(
            @PathVariable Long id,
            @Valid @RequestBody ServiceCatalogRequest request) {
        return ApiResponse.<ServiceCatalogResponse>builder()
                .code(200)
                .message("Service catalog updated successfully")
                .result(serviceCatalogService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete service catalog", description = "Admin can delete service catalog")
    public ApiResponse<Void> deleteServiceCatalog(@PathVariable Long id) {
        serviceCatalogService.delete(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Service catalog deleted successfully")
                .build();
    }
}
