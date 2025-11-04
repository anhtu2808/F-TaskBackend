package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.review.ReviewRequest;
import com.anhtu.ftaskbackend.dto.request.review.UpdateReviewRequest;
import com.anhtu.ftaskbackend.dto.response.review.ReviewResponse;

import java.util.List;

public interface ReviewService {
    
    /**
     * Customer tạo review cho partner sau khi booking completed
     */
    ReviewResponse createReview(Long userId, ReviewRequest request);
    
    /**
     * Update review (chỉ customer tạo mới được update)
     */
    ReviewResponse updateReview(Long userId, Long reviewId, UpdateReviewRequest request);
    
    /**
     * Lấy tất cả review của partner
     */
    List<ReviewResponse> getReviewsByPartner(Long partnerId);
    
    /**
     * Xóa review (chỉ customer tạo mới được xóa)
     */
    void deleteReview(Long userId, Long reviewId);
}

