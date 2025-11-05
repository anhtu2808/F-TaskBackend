package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.payment.CreatePaymentRequest;
import com.anhtu.ftaskbackend.dto.response.payment.PaymentResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface PaymentService {

    void confirmPayment(String vnp_OrderInfo, String vnp_ResponseCode, String vnp_TransactionStatus);

}
