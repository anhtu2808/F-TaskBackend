package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.wallet.WalletResponse;
import com.anhtu.ftaskbackend.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface WalletMapper {

    WalletResponse toWalletResponse(Wallet wallet);

}
