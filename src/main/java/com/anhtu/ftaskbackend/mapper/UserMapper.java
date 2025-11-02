package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.request.auth.RegisterRequest;
import com.anhtu.ftaskbackend.dto.request.auth.UpdateInformationRequest;
import com.anhtu.ftaskbackend.dto.request.user.CreateUserRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User registerRequestToUser(RegisterRequest request);

    UserResponse toUserResponse(User user);

    User updateInfoToUser(UpdateInformationRequest request, @MappingTarget User user);
}
