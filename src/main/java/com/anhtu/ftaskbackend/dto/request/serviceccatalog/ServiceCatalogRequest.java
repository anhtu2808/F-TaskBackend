package com.anhtu.ftaskbackend.dto.request.serviceccatalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCatalogRequest {


    String name;

    String description;

    String imageUrl;

    Double platformFeePercent;

    Boolean isActive;
}