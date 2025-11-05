package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.transaction.TransactionResponse;
import com.anhtu.ftaskbackend.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TransactionMapper {

    TransactionResponse toTransactionResponse(Transaction transaction);

}
