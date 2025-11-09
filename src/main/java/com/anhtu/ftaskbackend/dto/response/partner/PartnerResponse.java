package com.anhtu.ftaskbackend.dto.response.partner;

import com.anhtu.ftaskbackend.dto.response.district.DistrictResponse;
import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
    List<DistrictResponse> districts;
    UserResponse user;

}
