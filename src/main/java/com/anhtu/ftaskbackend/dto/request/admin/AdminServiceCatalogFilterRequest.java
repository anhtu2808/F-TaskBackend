package com.anhtu.ftaskbackend.dto.request.admin;

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
public class AdminServiceCatalogFilterRequest {
    
    String name;
    Boolean isActive;
    Double minPlatformFeePercent;
    Double maxPlatformFeePercent;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdTo;
    
    // Pagination
    Integer page = 0;
    Integer size = 20;
    String sortBy = "createAt";
    String sortDirection = "desc";
    
}
