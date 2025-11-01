package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.servicevariant.CreateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.request.servicevariant.UpdateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.response.ServiceVariant.ServiceVariantResponse;
import com.anhtu.ftaskbackend.entity.ServiceCatalog;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.ServiceVariantMapper;
import com.anhtu.ftaskbackend.repository.ServiceCatalogRepository;
import com.anhtu.ftaskbackend.repository.ServiceCatalogVariantRepository;
import com.anhtu.ftaskbackend.service.ServiceCatalogService;
import com.anhtu.ftaskbackend.service.ServiceVariantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServiceVariantServiceImpl implements ServiceVariantService {

    @Autowired
    ServiceCatalogVariantRepository serviceVariantRepository;
    @Autowired
    ServiceCatalogRepository serviceCatalogRepository;
    @Autowired
    ServiceVariantMapper serviceVariantMapper;


    @Override
    public ServiceVariantResponse createServiceVariant(CreateServiceVariantRequest request) {
        if (request.getDurationHours() <= 0)
            throw new AppException(ErrorCode.InvalidDurationHours);
        if (request.getPricePerVariant() <= 0)
            throw new AppException(ErrorCode.InvalidVariantPrice);
        if (request.getIsMultiPartner() && request.getNumberOfPartners() == 1)
            throw new AppException(ErrorCode.NumberOfPartnerMustBeGreaterOne);
        if (!request.getIsMultiPartner() && request.getNumberOfPartners() > 1)
            throw new AppException(ErrorCode.NumberOfPartnerMustBeOne);
        ServiceCatalog serviceCatalog = serviceCatalogRepository.findById(request.getServiceCatalogId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATALOG_NOT_FOUND));
        ServiceCatalogVariant variant = serviceVariantMapper.toVariant(request);
        variant.setServiceCatalog(serviceCatalog);
        serviceVariantRepository.save(variant);
        return serviceVariantMapper.toVariantResponse(variant);
    }

    @Override
    public Page<ServiceVariantResponse> findAllServiceVariants(Pageable pageable) {
        return null;
    }

    @Override
    public ServiceVariantResponse findServiceVariantById(Long id) {
        return null;
    }

    @Override
    public ServiceVariantResponse updateServiceVariant(Long id, UpdateServiceVariantRequest request) {
        return null;
    }

    @Override
    public void deleteServiceVariant(Long id) {

    }
}
