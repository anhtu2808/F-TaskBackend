package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerFilterRequest;
import com.anhtu.ftaskbackend.dto.response.partner.PartnerResponse;
import com.anhtu.ftaskbackend.dto.response.transaction.TransactionResponse;
import com.anhtu.ftaskbackend.enums.TransactionStatus;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/admin/transactions")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Admin Transaction")
public class AdminTransactionController {

    TransactionService transactionService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all transactions with filters",
            description = "Get paginated list of transactions with comprehensive filtering options for admin")
    public ApiResponse<Page<TransactionResponse>> getAllTransactions(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) TransactionType type) {
        return ApiResponse.<Page<TransactionResponse>>builder()
                .code(200)
                .message("Transactions retrieved successfully")
                .result(transactionService.getAllTransactions(page - 1, size, type))
                .build();
    }

    @GetMapping("/total-fee")
    public ApiResponse<Double> getAllTotalFee() {
        return ApiResponse.<Double>builder()
                .code(200)
                .message("Transactions retrieved successfully")
                .result(transactionService.getAllTotalFee())
                .build();
    }

}
