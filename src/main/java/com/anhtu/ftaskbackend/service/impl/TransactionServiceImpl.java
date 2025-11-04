package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.transaction.CreateTransactionRequest;
import com.anhtu.ftaskbackend.dto.request.transaction.TransactionParam;
import com.anhtu.ftaskbackend.dto.response.transaction.TransactionResponse;
import com.anhtu.ftaskbackend.entity.BookingPartner;
import com.anhtu.ftaskbackend.entity.Transaction;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.entity.Wallet;
import com.anhtu.ftaskbackend.enums.TransactionStatus;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.mapper.TransactionMapper;
import com.anhtu.ftaskbackend.repository.*;
import com.anhtu.ftaskbackend.service.TransactionService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    BookingPartnerRepository bookingPartnerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    TransactionMapper transactionMapper;

    @Override
    public Long createTransaction(CreateTransactionRequest request) {
        Long userId = JWTHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));
        Wallet wallet = user.getWallet();
        TransactionType type = request.getType();
        BookingPartner bookingPartner = request.getBookingPartnerId() != null
                ? bookingPartnerRepository.findById(request.getBookingPartnerId())
                        .orElseThrow(() -> new AppException(ErrorCode.BookingPartnerNotFound))
                : null;
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(type)
                .description(request.getDescription())
                .bookingPartner(bookingPartner)
                .balanceBefore(wallet.getBalance())
                .status(TransactionStatus.PENDING)
                .user(user)
                .build();
        switch (type) {
            case WITHDRAWAL, FINE -> wallet.setBalance(wallet.getBalance() - request.getAmount());
            case TOP_UP, EARNING -> wallet.setBalance(wallet.getBalance() + request.getAmount());
        }
        transaction.setBalanceAfter(wallet.getBalance());
        transactionRepository.save(transaction);
        walletRepository.save(wallet);
        return transaction.getId();
    }

    @Override
    public Page<TransactionResponse> getTransactionsByUserId(Long userId, int page, int size) {
        return null;
    }

    @Override
    public TransactionResponse getTransactionById(Long transactionId) {
        return null;
    }
}
