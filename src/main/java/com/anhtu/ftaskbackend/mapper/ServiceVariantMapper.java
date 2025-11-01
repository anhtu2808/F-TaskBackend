package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.request.servicevariant.CreateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.request.servicevariant.UpdateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.response.ServiceVariant.ServiceVariantResponse;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import org.mapstruct.*;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface ServiceVariantMapper {

    @Mapping(source = "isMultiPartner", target = "isMultiPartner")
    ServiceVariantResponse toVariantResponse(ServiceCatalogVariant variant);

    @Mapping(source = "isMultiPartner", target = "isMultiPartner")
    ServiceCatalogVariant toVariant(CreateServiceVariantRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ServiceCatalogVariant updateToVariant(UpdateServiceVariantRequest request, @MappingTarget ServiceCatalogVariant variant);

}
