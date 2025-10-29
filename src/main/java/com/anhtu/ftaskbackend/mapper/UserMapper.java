package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.request.auth.RegisterRequest;
import com.anhtu.ftaskbackend.dto.request.user.CreateUserRequest;
import com.anhtu.ftaskbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User registerRequestToUser(RegisterRequest request);

}
