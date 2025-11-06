package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.Wallet.AdjustWalletBalanceRequest;
import com.anhtu.ftaskbackend.dto.request.payment.CreatePaymentRequest;
import com.anhtu.ftaskbackend.dto.response.payment.PaymentResponse;
import com.anhtu.ftaskbackend.enums.TransactionType;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.repository.PaymentRepository;
import com.anhtu.ftaskbackend.repository.TransactionRepository;
import com.anhtu.ftaskbackend.service.PaymentService;
import com.anhtu.ftaskbackend.service.WalletService;
import com.anhtu.ftaskbackend.thirdParty.VNPayAPI;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    WalletService walletService;

    @Override
    public void confirmPayment(String orderInfo, String vnp_ResponseCode, String vnp_TransactionStatus) {

        if (orderInfo == null || orderInfo.isEmpty()) {
            throw new AppException(ErrorCode.InvalidOrderInfo);
        }

        try {
            String[] parts = orderInfo.split("_");

            if (parts.length < 6) {
                throw new AppException(ErrorCode.InvalidOrderInfo);
            }
            Long userId = Long.parseLong(parts[1]);
            String typePayment = parts[3];
            Double amount = Double.parseDouble(parts[5]);

            System.out.println("User ID: " + userId);
            System.out.println("Type: " + typePayment);
            System.out.println("Amount: " + amount);

            switch (typePayment) {
                case "TOPUP" -> {
                    walletService.adjustBalance(userId, AdjustWalletBalanceRequest.builder().amount(amount).type(TransactionType.TOP_UP).build());
                }
                case "WITHDRAWAL" -> {
                    walletService.adjustBalance(userId, AdjustWalletBalanceRequest.builder().amount(amount).type(TransactionType.WITHDRAWAL).build());
                }
                case "PAYMENT" -> {
                    // logic thanh toán dịch vụ khác
                }
                default -> throw new AppException(ErrorCode.UnknownType);
            }

        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.InvalidOrderInfo);
        }
    }

}
