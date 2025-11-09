package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.partner.PartnerResponse;
import com.anhtu.ftaskbackend.entity.Partner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, DistrictMapper.class})
public interface PartnerMapper {

    @Mapping(target = "districts", source = "districts")
    PartnerResponse toPartnerResponse(Partner partner);
}
