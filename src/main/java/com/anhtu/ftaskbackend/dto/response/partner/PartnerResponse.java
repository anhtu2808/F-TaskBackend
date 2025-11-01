package com.anhtu.ftaskbackend.dto.response.partner;

import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import com.anhtu.ftaskbackend.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartnerResponse {

    Long id;
    Double averageRating;
    Integer totalJobsCompleted;
    Boolean isAvailable;
    String districtIdsJson;
    UserResponse user;

}
