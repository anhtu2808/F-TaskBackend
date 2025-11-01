package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.user.id = :id AND c.user.deleted = 0")
    Optional<Customer> findByUser_Id(@Param("id") Long id);

}
