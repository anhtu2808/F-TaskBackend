package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerFilterRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerStatusUpdateRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerDistrictsRequest;
import com.anhtu.ftaskbackend.dto.response.partner.PartnerResponse;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.service.PartnerService;
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
@RequestMapping("/admin/partners")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Admin Partners", description = "Admin partner management APIs")
public class AdminPartnerController {

    PartnerService partnerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all partners with filters", description = "Get paginated list of partners with comprehensive filtering options for admin")
    public ApiResponse<Page<PartnerResponse>> getAllPartners(@ParameterObject AdminPartnerFilterRequest filter) {
        return ApiResponse.<Page<PartnerResponse>>builder()
                .code(200)
                .message("Partners retrieved successfully")
                .result(partnerService.getAllPartnersForAdmin(filter))
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get partner details", description = "Get detailed information about a specific partner")
    public ApiResponse<PartnerResponse> getPartnerById(@PathVariable Long id) {
        return ApiResponse.<PartnerResponse>builder()
                .code(200)
                .message("Partner details retrieved successfully")
                .result(partnerService.adminGetPartnerById(id))
                .build();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update partner status", description = "Admin can update partner availability status")
    public ApiResponse<Void> updatePartnerStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminPartnerStatusUpdateRequest request) {
        partnerService.adminUpdatePartnerStatus(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Partner status updated successfully")
                .build();
    }

    @PutMapping("/{id}/districts")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update partner districts", description = "Admin can manage partner's working districts")
    public ApiResponse<Void> updatePartnerDistricts(
            @PathVariable Long id,
            @Valid @RequestBody AdminPartnerDistrictsRequest request) {
        partnerService.adminUpdatePartnerDistricts(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Partner districts updated successfully")
                .build();
    }

    @GetMapping("/{id}/bookings")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get partner's bookings", description = "Get paginated list of bookings for a specific partner")
    public ApiResponse<Page<BookingResponse>> getPartnerBookings(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.<Page<BookingResponse>>builder()
                .code(200)
                .message("Partner bookings retrieved successfully")
                .result(partnerService.adminGetPartnerBookings(id, page, size))
                .build();
    }
}
