package com.anhtu.ftaskbackend.dto.request.admin;

import com.anhtu.ftaskbackend.enums.Gender;
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
public class AdminUserFilterRequest {
    
    String fullName;
    String phone;
    String email;
    String username;
    Boolean isActive;
    Gender gender;
    String roleName;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdTo;
    
    // Pagination
    Integer page = 0;
    Integer size = 20;
    String sortBy = "createdAt";
    String sortDirection = "desc";
    
}
