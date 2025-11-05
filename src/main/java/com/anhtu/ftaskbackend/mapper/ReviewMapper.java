package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.request.review.ReviewRequest;
import com.anhtu.ftaskbackend.dto.response.review.ReviewResponse;
import com.anhtu.ftaskbackend.entity.Review;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "partner", ignore = true)
    Review toEntity(ReviewRequest request);

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.user.fullName", target = "customerName")
    @Mapping(source = "partner.id", target = "partnerId")
    @Mapping(source = "partner.user.fullName", target = "partnerName")
    @Mapping(source = "createAt", target = "createdAt")
    ReviewResponse toResponse(Review entity);
}

