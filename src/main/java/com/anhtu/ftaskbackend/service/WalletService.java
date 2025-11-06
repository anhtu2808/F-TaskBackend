package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.Wallet.AdjustWalletBalanceRequest;
import com.anhtu.ftaskbackend.dto.response.wallet.WalletResponse;
import com.anhtu.ftaskbackend.enums.TransactionType;

public interface WalletService {

    void adjustBalance(Long userId, AdjustWalletBalanceRequest request);
    WalletResponse getMyWallet(Long userId);

}
