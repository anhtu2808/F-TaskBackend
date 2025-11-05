package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
