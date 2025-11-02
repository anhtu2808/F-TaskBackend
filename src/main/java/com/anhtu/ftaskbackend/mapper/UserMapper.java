package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.request.auth.RegisterRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserInfoResponse;
import com.anhtu.ftaskbackend.dto.request.auth.UpdateInformationRequest;
import com.anhtu.ftaskbackend.dto.request.user.CreateUserRequest;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class}
)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User registerRequestToUser(RegisterRequest request);

    @Mapping(source = "role.name", target = "role")
    UserResponse toUserResponse(User user);

    @Mapping(source = "role.name", target = "role")
    @Mapping(source = "partner.id", target = "partnerId")
    @Mapping(source = "partner.averageRating", target = "averageRating")
    @Mapping(source = "partner.totalJobsCompleted", target = "totalJobsCompleted")
    @Mapping(source = "customer.id", target = "customerId")
    UserInfoResponse toUserInfoResponse(User user);

    User updateInfoToUser(UpdateInformationRequest request, @MappingTarget User user);
}
