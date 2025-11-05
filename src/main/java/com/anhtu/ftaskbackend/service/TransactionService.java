package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.transaction.CreateTransactionRequest;
import com.anhtu.ftaskbackend.dto.request.transaction.TransactionParam;
import com.anhtu.ftaskbackend.dto.response.transaction.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    Long createTransaction(CreateTransactionRequest request);
    Page<TransactionResponse> getTransactionsByUserId(Long userId, int page, int size);
    TransactionResponse getTransactionById(Long transactionId);

}
