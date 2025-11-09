package com.anhtu.ftaskbackend.dto.request.partner;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDistrictsRequest {

    @NotEmpty(message = "District IDs cannot be empty")
    List<Long> districtIds;

}

