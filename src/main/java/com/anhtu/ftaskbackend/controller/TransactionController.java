package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.transaction.CreateTransactionRequest;
import com.anhtu.ftaskbackend.dto.request.transaction.TransactionParam;
import com.anhtu.ftaskbackend.dto.response.transaction.TransactionResponse;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.TransactionService;
import com.anhtu.ftaskbackend.thirdParty.VNPayAPI;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/transactions")
@FieldDefaults(level = PRIVATE)
public class TransactionController {
    @Autowired
    VNPayAPI vnPayAPI;
    @Autowired
    TransactionService transactionService;

    @PostMapping("/top-up")
    public ApiResponse<Map<String, Object>> topUp(@ParameterObject TransactionParam param) {
        String info = "User_" + JWTHelper.getCurrentUserId() + "_recharge_" + param.getAmount() + "_VND_";
        Long id = transactionService.createTransaction(CreateTransactionRequest.builder()
                        .description(info)
                        .amount(param.getAmount())
                        .type(TransactionType.TOP_UP)
                        .build());
        info = "_TOPUP_" + info + "Transaction_Code" + id;
        return ApiResponse.<Map<String, Object>>builder()
                .message("Top up transaction successful")
                .result(vnPayAPI.createPayment(param, info, TransactionType.TOP_UP.toString()))
                .build();
    }

    @PostMapping("/withdrawal")
    public ApiResponse<Map<String, Object>> withdrawal(@ParameterObject TransactionParam param) {
        String info = "User_" +JWTHelper.getCurrentUserId() + "_withdraw_" + param.getAmount() + "_VND_";
        Long id = transactionService.createTransaction(CreateTransactionRequest.builder()
                .description("info")
                .amount(param.getAmount())
                .type(TransactionType.WITHDRAWAL)
                .build());
        info = "_WITHDRAWAL_" + info + "Transaction_Code" + id;
        return ApiResponse.<Map<String, Object>>builder()
                .message("Withdrawal transaction successful")
                .result(vnPayAPI.createPayment(param, info, TransactionType.WITHDRAWAL.toString()))
                .build();
    }
}
