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


}
