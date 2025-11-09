package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog,Long>, JpaSpecificationExecutor<ServiceCatalog> {
}
