package com.anhtu.ftaskbackend.mapper;


import com.anhtu.ftaskbackend.dto.request.serviceccatalog.ServiceCatalogRequest;
import com.anhtu.ftaskbackend.dto.response.servicecatelog.ServiceCatalogResponse;
import com.anhtu.ftaskbackend.entity.ServiceCatalog;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ServiceCatalogMapper {

    ServiceCatalog toEntity(ServiceCatalogRequest request);

    ServiceCatalogResponse toResponse(ServiceCatalog entity);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ServiceCatalogRequest request, @MappingTarget ServiceCatalog entity);
}
