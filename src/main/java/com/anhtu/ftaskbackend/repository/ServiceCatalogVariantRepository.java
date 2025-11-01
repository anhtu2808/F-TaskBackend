package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCatalogVariantRepository extends JpaRepository<ServiceCatalogVariant, Long> {
}
