package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.Customer.CustomerResponse;
import com.anhtu.ftaskbackend.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CustomerMapper {

    CustomerResponse toCustomerResponse(Customer customer);

}
