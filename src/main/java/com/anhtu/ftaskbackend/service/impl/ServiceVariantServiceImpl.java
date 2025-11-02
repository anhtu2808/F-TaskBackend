package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.servicevariant.CreateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.request.servicevariant.FilterServiceVariant;
import com.anhtu.ftaskbackend.dto.request.servicevariant.UpdateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.response.ServiceVariant.ServiceVariantResponse;
import com.anhtu.ftaskbackend.entity.ServiceCatalog;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.ServiceVariantMapper;
import com.anhtu.ftaskbackend.repository.ServiceCatalogRepository;
import com.anhtu.ftaskbackend.repository.ServiceCatalogVariantRepository;
import com.anhtu.ftaskbackend.repository.specification.BookingSpecification;
import com.anhtu.ftaskbackend.repository.specification.ServiceCatalogVariantSpecification;
import com.anhtu.ftaskbackend.service.ServiceCatalogService;
import com.anhtu.ftaskbackend.service.ServiceVariantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Page<ServiceVariantResponse> findAllServiceVariants(FilterServiceVariant params) {
        var spec = ServiceCatalogVariantSpecification.filter(params);
        var pageable = PageRequest.of(params.getPage() - 1, params.getSize());
        return serviceVariantRepository.findAll(spec, pageable)
                .map(serviceVariantMapper::toVariantResponse);
    }

    @Override
    public ServiceVariantResponse findServiceVariantById(Long id) {
        return serviceVariantMapper.toVariantResponse(getServiceCatalogService(id));
    }

    @Override
    public ServiceVariantResponse updateServiceVariant(Long id, UpdateServiceVariantRequest request) {
        var variant = getServiceCatalogService(id);
        serviceVariantRepository.save(serviceVariantMapper.updateToVariant(request, variant));
        return serviceVariantMapper.toVariantResponse(variant);
    }

    @Override
    public void deleteServiceVariant(Long id) {
        var variant = getServiceCatalogService(id);
        serviceVariantRepository.delete(variant);
    }

    private ServiceCatalogVariant getServiceCatalogService(Long serviceCatalogId) {
        return serviceVariantRepository.findById(serviceCatalogId)
                .orElseThrow(() -> new AppException(ErrorCode.ServiceVariantNotFound));
    }
}
