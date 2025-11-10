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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    TransactionMapper transactionMapper;
    @Autowired
    BookingRepository bookingRepository;

    @Override
    public Long createTransaction(CreateTransactionRequest request) {
        BookingPartner bookingPartner = request.getBookingPartnerId() != null
                ? bookingPartnerRepository.findById(request.getBookingPartnerId())
                        .orElseThrow(() -> new AppException(ErrorCode.BookingPartnerNotFound))
                : null;
        String description = getDescription(request);
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .description(description)
                .bookingPartner(bookingPartner)
                .balanceBefore(request.getBalanceBefore())
                .balanceAfter(request.getBalanceAfter())
                .status(TransactionStatus.COMPLETED)
                .user(request.getUser())
                .build();
        transactionRepository.save(transaction);
        return transaction.getId();
    }

    @NotNull
    private String getDescription(CreateTransactionRequest request) {
        String description = "";
        switch (request.getType()) {
            case TOP_UP -> {
                description += "Bạn vừa nạp "
                        + request.getAmount()
                        + " VNĐ vào tài khoản.";
            }
            case EARNING -> {
                description += "Bạn vừa nhận được "
                        + request.getAmount()
                        + " VNĐ vào tài khoản vì hoàn thành công việc.";
            }
            case FINE -> {
                description += "Bạn vừa bị phạt "
                        + request.getAmount()
                        + " VNĐ vì huỷ gói booking "
                        + request.getBookingId()
                        + " trong khoảng 6 tiếng trước khi công việc bắt đầu.";
            }
            case PLATFORM_FEE -> {
                description += "Phí hoa hồng cho nền tảng"
                        + request.getAmount()
                        + " VNĐ.";
            }
            case WITHDRAWAL -> {
                description += "Bạn vừa rút "
                        + request.getAmount()
                        + " VNĐ.";
            }
            case ADJUSTMENT -> {
                description += "Bạn vừa thanh toán "
                        + request.getAmount()
                        + " VNĐ cho booking "
                        + request.getBookingId() + ".";
            }
            case REFUND -> {
                description += "Bạn vừa được hoàn "
                        + request.getAmount() + " VNĐ ";
                if (request.getUser().getId().equals(bookingRepository.findById(request.getBookingId())
                        .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound)).getCustomer().getUser().getId()))
                    description += request.getBookingId() + ".";
                else
                    description += "vì khách hàng đã huỷ gói booking "
                            + request.getBookingId() + ".";
            }
        }
        description += " Số dư hiện tại: " + request.getBalanceAfter();
        return description;
    }

    @Override
    public Page<TransactionResponse> getTransactionsByUserId(Long userId, int page, int size) {
        var pageable = PageRequest.of(page - 1, size);
        Page<Transaction> transactions = transactionRepository.findTransactionByUser_Id(userId, pageable);
        return transactions.map(transaction -> transactionMapper.toTransactionResponse(transaction));
    }

    @Override
    public TransactionResponse getTransactionById(Long transactionId) {
        return null;
    }

    @Override
    public Page<TransactionResponse> getAllTransactions(int page, int size, TransactionType transactionType) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<Transaction> transactions;

        if (transactionType != null) {
            transactions = transactionRepository.findByType(transactionType, pageable);
        } else {
            transactions = transactionRepository.findAll(pageable);
        }

        return transactions.map(transactionMapper::toTransactionResponse);
    }

    @Override
    public Double getAllTotalFee() {
        return transactionRepository.sumAmountByTransactionType(TransactionType.PLATFORM_FEE);
    }
}
