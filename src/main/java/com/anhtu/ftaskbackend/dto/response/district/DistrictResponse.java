package com.anhtu.ftaskbackend.dto.response.district;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DistrictResponse {

    Long id;
    String name;
    String city;
    String code;

}

