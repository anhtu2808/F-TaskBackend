package com.anhtu.ftaskbackend.service.impl;


import com.anhtu.ftaskbackend.dto.request.serviceccatalog.ServiceCatalogRequest;
import com.anhtu.ftaskbackend.dto.response.servicecatelog.ServiceCatalogResponse;
import com.anhtu.ftaskbackend.entity.ServiceCatalog;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.ServiceCatalogMapper;
import com.anhtu.ftaskbackend.repository.ServiceCatalogRepository;
import com.anhtu.ftaskbackend.service.ServiceCatalogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    ServiceCatalogRepository repository;
    ServiceCatalogMapper mapper;

    @Override
    public ServiceCatalogResponse create(ServiceCatalogRequest request) {
        ServiceCatalog entity = mapper.toEntity(request);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    public ServiceCatalogResponse update(Long id, ServiceCatalogRequest request) {
        ServiceCatalog entity = repository.findById(id)
                                          .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATALOG_NOT_FOUND));

        // ⚙️ MapStruct update only non-null fields
        mapper.updateEntityFromRequest(request, entity);
        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public ServiceCatalogResponse getById(Long id) {
        ServiceCatalog entity = repository.findById(id)
                                          .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATALOG_NOT_FOUND));
        return mapper.toResponse(entity);
    }

    @Override
    public List<ServiceCatalogResponse> getAll() {
        return repository.findAll()
                         .stream()
                         .map(mapper::toResponse)
                         .toList();
    }

    @Override
    public void delete(Long id) {
        ServiceCatalog entity = repository.findById(id)
                                          .orElseThrow(() -> new IllegalArgumentException("Service Catalog not found with id: " + id));
        repository.delete(entity);
    }
}
