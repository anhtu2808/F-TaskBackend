package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.servicevariant.CreateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.request.servicevariant.UpdateServiceVariantRequest;
import com.anhtu.ftaskbackend.dto.response.ServiceVariant.ServiceVariantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceVariantService {

    ServiceVariantResponse createServiceVariant(CreateServiceVariantRequest request);
    Page<ServiceVariantResponse> findAllServiceVariants(Pageable pageable);
    ServiceVariantResponse findServiceVariantById(Long id);
    ServiceVariantResponse updateServiceVariant(Long id, UpdateServiceVariantRequest request);
    void deleteServiceVariant(Long id);

}
