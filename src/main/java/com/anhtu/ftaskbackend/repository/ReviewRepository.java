package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByPartnerId(Long partnerId);
    
    List<Review> findByCustomerId(Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.booking.id = :bookingId AND r.partner.id = :partnerId")
    Optional<Review> findByBookingIdAndPartnerId(@Param("bookingId") Long bookingId, @Param("partnerId") Long partnerId);
    
    boolean existsByBookingIdAndPartnerId(Long bookingId, Long partnerId);
}

