package com.anhtu.ftaskbackend.dto.request.admin;

import com.anhtu.ftaskbackend.enums.BookingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminBookingFilterRequest {
    
    BookingStatus status;
    Long customerId;
    Long partnerId;
    Long serviceCatalogId;
    Long variantId;
    String customerName;
    String partnerName;
    String serviceName;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime startDateFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime startDateTo;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdTo;
    
    Double minPrice;
    Double maxPrice;
    String district;
    Boolean isCustomerAccepted;
    
    // Pagination
    Integer page = 0;
    Integer size = 20;
    String sortBy = "createdAt";
    String sortDirection = "desc";
    
}
