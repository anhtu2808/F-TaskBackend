package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.dto.request.review.ReviewRequest;
import com.anhtu.ftaskbackend.dto.request.review.UpdateReviewRequest;
import com.anhtu.ftaskbackend.dto.response.review.ReviewResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Review", description = "Review management APIs")
public class ReviewController {

    ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create review for partner (must be completed booking)")
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        Long userId = JWTHelper.getCurrentUserId();
        ReviewResponse response = reviewService.createReview(userId, request);
        return ApiResponse.<ReviewResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Review created successfully")
                .result(response)
                .build();
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update review (only by review creator)")
    public ApiResponse<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        Long userId = JWTHelper.getCurrentUserId();
        ReviewResponse response = reviewService.updateReview(userId, reviewId, request);
        return ApiResponse.<ReviewResponse>builder()
                .message("Review updated successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete review (only by review creator)")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId) {
        Long userId = JWTHelper.getCurrentUserId();
        reviewService.deleteReview(userId, reviewId);
        return ApiResponse.<Void>builder()
                .message("Review deleted successfully")
                .build();
    }
}

