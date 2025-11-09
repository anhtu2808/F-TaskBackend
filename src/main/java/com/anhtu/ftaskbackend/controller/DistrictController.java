package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.response.district.DistrictResponse;
import com.anhtu.ftaskbackend.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/districts")
@Tag(name = "District", description = "APIs for Districts")
public class DistrictController {

    private final PartnerService partnerService;

    @GetMapping
    @Operation(summary = "Get all available districts", description = "Public endpoint to get all districts for selection")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<DistrictResponse>> getAllDistricts() {
        List<DistrictResponse> response = partnerService.getAllDistricts();
        return ApiResponse.<List<DistrictResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all districts successfully")
                .result(response)
                .build();
    }
}

