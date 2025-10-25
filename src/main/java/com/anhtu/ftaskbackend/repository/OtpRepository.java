package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByOtpCodeAndIsUsedFalse(String otpCode);

}
