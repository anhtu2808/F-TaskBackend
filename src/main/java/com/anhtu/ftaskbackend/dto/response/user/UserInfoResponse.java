package com.anhtu.ftaskbackend.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {
    Long id;
    String username;
    String fullName;
    String email;
    String phone;
    String avatarUrl;
    String role;
    Long partnerId;
    Double averageRating;
    Integer totalJobsCompleted;
    Long customerId;
}
