package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.payment.CreatePaymentRequest;
import com.anhtu.ftaskbackend.dto.response.payment.PaymentResponse;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.repository.PaymentRepository;
import com.anhtu.ftaskbackend.repository.TransactionRepository;
import com.anhtu.ftaskbackend.service.PaymentService;
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

    @Override
    public void confirmPayment(String orderInfo, String vnp_ResponseCode, String vnp_TransactionStatus) {

        String type = null;
        if (orderInfo.startsWith("_")) {
            int firstUnderscore = orderInfo.indexOf("_");
            int secondUnderscore = orderInfo.indexOf("_", firstUnderscore + 1);
            if (secondUnderscore > 0) {
                type = orderInfo.substring(firstUnderscore + 1, secondUnderscore);
            }
        }

        String id = null;
        int codeIndex = orderInfo.lastIndexOf("Code");
        if (codeIndex != -1) {
            id = orderInfo.substring(codeIndex + 4);
        }

        if (type == null || id == null || id.isEmpty()) {
            throw new AppException(ErrorCode.InvalidOrderInfo);
        }

        System.out.println("Type: " + type);
        System.out.println("ID: " + id);

        switch (type) {
            case "TOPUP", "WITHDRAWAL" -> {

            }
            case "PAYMENT" -> {

            }
            default -> throw new AppException(ErrorCode.UnknownType);
        }
    }

}
