package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    
    Optional<District> findByName(String name);
    
    List<District> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT d FROM District d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<District> searchByName(@Param("name") String name);
    
}

