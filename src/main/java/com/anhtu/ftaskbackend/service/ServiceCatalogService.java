package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.serviceccatalog.ServiceCatalogRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminServiceCatalogFilterRequest;
import com.anhtu.ftaskbackend.dto.response.servicecatelog.ServiceCatalogResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ServiceCatalogService {

    ServiceCatalogResponse create(ServiceCatalogRequest request);

    ServiceCatalogResponse update(Long id, ServiceCatalogRequest request);

    ServiceCatalogResponse getById(Long id);

    List<ServiceCatalogResponse> getAll();

    void delete(Long id);
    
    // Admin methods
    Page<ServiceCatalogResponse> getAllForAdmin(AdminServiceCatalogFilterRequest filter);
}
