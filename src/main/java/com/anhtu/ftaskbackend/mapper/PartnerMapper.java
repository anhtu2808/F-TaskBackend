package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.partner.PartnerResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.entity.Partner;
import com.anhtu.ftaskbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PartnerMapper {

    PartnerResponse toPartnerResponse(Partner partner);
}
