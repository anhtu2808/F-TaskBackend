package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.Wallet.AdjustWalletBalanceRequest;
import com.anhtu.ftaskbackend.dto.request.transaction.CreateTransactionRequest;
import com.anhtu.ftaskbackend.dto.request.transaction.TransactionParam;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.TransactionService;
import com.anhtu.ftaskbackend.service.WalletService;
import com.anhtu.ftaskbackend.thirdParty.VNPayAPI;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/wallets")
@FieldDefaults(level = PRIVATE)
public class WalletController {

    @Autowired
    VNPayAPI vnPayAPI;
    @Autowired
    WalletService walletService;

    @PostMapping("/top-up")
    public ApiResponse<Map<String, Object>> topUp(@RequestParam Double amount, @RequestParam String callbackUrl) {
        String info = "User_" + JWTHelper.getCurrentUserId() + "_type_TOPUP_amount_" + amount;
        return ApiResponse.<Map<String, Object>>builder()
                .message("Top up wallet successful")
                .result(vnPayAPI.createPayment(amount, info, TransactionType.TOP_UP.toString(), callbackUrl))
                .build();
    }

    @PostMapping("/withdrawal")
    public ApiResponse<Void> withdrawal(@RequestParam Double amount) {
        walletService.adjustBalance(JWTHelper.getCurrentUserId(),
                AdjustWalletBalanceRequest.builder()
                        .type(TransactionType.WITHDRAWAL)
                        .amount(amount)
                        .build());
        return ApiResponse.<Void>builder()
                .message("Withdrawal wallet successful")
                .build();
    }

}
