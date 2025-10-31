package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.Customer.AddressResponse;
import com.anhtu.ftaskbackend.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse toAddressResponse(Address address);

}
