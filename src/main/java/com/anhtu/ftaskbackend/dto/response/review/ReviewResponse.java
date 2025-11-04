package com.anhtu.ftaskbackend.dto.response.review;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    
    Long id;
    Long bookingId;
    Long customerId;
    String customerName;
    Long partnerId;
    String partnerName;
    Integer rating;
    String description;
    LocalDateTime createdAt;
}

