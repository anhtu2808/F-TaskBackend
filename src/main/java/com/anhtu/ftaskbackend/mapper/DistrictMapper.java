package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.district.DistrictResponse;
import com.anhtu.ftaskbackend.entity.District;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface DistrictMapper {

    DistrictResponse toDistrictResponse(District district);

    List<DistrictResponse> toDistrictResponseList(List<District> districts);

    List<DistrictResponse> toDistrictResponseListFromSet(Set<District> districts);

}

