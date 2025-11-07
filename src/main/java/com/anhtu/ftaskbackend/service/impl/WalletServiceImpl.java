package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.Wallet.AdjustWalletBalanceRequest;
import com.anhtu.ftaskbackend.dto.request.transaction.CreateTransactionRequest;
import com.anhtu.ftaskbackend.dto.response.wallet.WalletResponse;
import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.entity.Wallet;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.mapper.WalletMapper;
import com.anhtu.ftaskbackend.repository.UserRepository;
import com.anhtu.ftaskbackend.repository.WalletRepository;
import com.anhtu.ftaskbackend.service.TransactionService;
import com.anhtu.ftaskbackend.service.WalletService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletServiceImpl implements WalletService {

    @Autowired
    WalletRepository walletRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WalletMapper walletMapper;
    @Autowired
    TransactionService transactionService;

    @Override
    public void adjustBalance(Long userId, AdjustWalletBalanceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));
        Wallet wallet = user.getWallet();
        Double balanceBefore = wallet.getBalance();
        switch (request.getType()){
            case TOP_UP, EARNING, REFUND -> wallet.setBalance(balanceBefore + request.getAmount());
            case WITHDRAWAL, FINE, PLATFORM_FEE, ADJUSTMENT -> wallet.setBalance(balanceBefore - request.getAmount());
        }
        Double balanceAfter = wallet.getBalance();
        walletRepository.save(wallet);
        transactionService.createTransaction(CreateTransactionRequest.builder()
                        .bookingPartnerId(request.getBookingPartnerId())
                        .type(request.getType())
                        .balanceBefore(balanceBefore)
                        .balanceAfter(balanceAfter)
                        .amount(request.getAmount())
                        .bookingId(request.getBookingId())
                        .user(user)
                .build());
    }

    @Override
    public WalletResponse getMyWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));
        Wallet wallet = user.getWallet();
        return walletMapper.toWalletResponse(wallet);
    }
}
