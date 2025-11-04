package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    
    @Query("SELECT DISTINCT p FROM Partner p JOIN p.districts d " +
           "WHERE (LOWER(d) LIKE LOWER(CONCAT('%', :district, '%')) " +
           "OR LOWER(:district) LIKE LOWER(CONCAT('%', d, '%'))) " +
           "AND p.isAvailable = true")
    List<Partner> findAvailablePartnersByDistrict(@Param("district") String district);
}
