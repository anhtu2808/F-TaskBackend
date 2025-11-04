package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.review.ReviewRequest;
import com.anhtu.ftaskbackend.dto.response.review.ReviewResponse;
import com.anhtu.ftaskbackend.entity.*;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.ReviewMapper;
import com.anhtu.ftaskbackend.repository.*;
import com.anhtu.ftaskbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    ReviewRepository reviewRepository;
    BookingRepository bookingRepository;
    CustomerRepository customerRepository;
    PartnerRepository partnerRepository;
    ReviewMapper mapper;

    @Override
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new AppException(ErrorCode.BookingNotCompleted);
        }

        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));

        boolean isPartnerInBooking = booking.getPartners().stream()
                .anyMatch(bp -> bp.getPartner().getId().equals(partner.getId()));
        
        if (!isPartnerInBooking) {
            throw new AppException(ErrorCode.PartnerNotInBooking);
        }

        if (reviewRepository.existsByBookingIdAndPartnerId(booking.getId(), partner.getId())) {
            throw new AppException(ErrorCode.ReviewAlreadyExists);
        }

        // Tạo review
        Review review = mapper.toEntity(request);
        review.setBooking(booking);
        review.setCustomer(customer);
        review.setPartner(partner);

        reviewRepository.save(review);

        updatePartnerAverageRating(partner.getId());

        log.info("Customer {} created review for partner {} on booking {}", 
                 customer.getId(), partner.getId(), booking.getId());

        return mapper.toResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByPartner(Long partnerId) {
        if (!partnerRepository.existsById(partnerId)) {
            throw new AppException(ErrorCode.PartnerNotFound);
        }

        return reviewRepository.findByPartnerId(partnerId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void deleteReview(Long userId, Long reviewId) {
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.ReviewNotFound));

        if (!review.getCustomer().getId().equals(customer.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Long partnerId = review.getPartner().getId();
        reviewRepository.delete(review);

        // Cập nhật lại rating trung bình của partner
        updatePartnerAverageRating(partnerId);

        log.info("Customer {} deleted review {}", customer.getId(), reviewId);
    }

    private void updatePartnerAverageRating(Long partnerId) {
        List<Review> reviews = reviewRepository.findByPartnerId(partnerId);
        
        if (reviews.isEmpty()) {
            Partner partner = partnerRepository.findById(partnerId)
                    .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));
            partner.setAverageRating(0.0);
            partnerRepository.save(partner);
            return;
        }

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new AppException(ErrorCode.PartnerNotFound));
        partner.setAverageRating(Math.round(averageRating * 10.0) / 10.0);
        partnerRepository.save(partner);

        log.info("Updated partner {} average rating to {}", partnerId, partner.getAverageRating());
    }
}

