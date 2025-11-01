package com.anhtu.ftaskbackend.dto.request.servicevariant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateServiceVariantRequest {

    String name;
    String description;
    Integer durationHours;
    Double pricePerVariant;
    Boolean isMultiPartner;
    Integer numberOfPartners;
    Integer serviceCatalogId;
}
