package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.ServiceVariant.ServiceVariantResponse;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceVariantMapper {

    @Mapping(source = "isMultiPartner", target = "isMultiPartner")
    ServiceVariantResponse toVariantResponse(ServiceCatalogVariant variant);

}
