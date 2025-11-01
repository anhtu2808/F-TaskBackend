package com.anhtu.ftaskbackend.dto.request.servicevariant;

import jakarta.persistence.Column;
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
public class CreateServiceVariantRequest {

    @NotNull @NotBlank
    String name;
    String description;
    @NotNull
    Integer durationHours;
    @NotNull
    Double pricePerVariant;
    @NotNull
    Boolean isMultiPartner;
    @NotNull
    Integer numberOfPartners;
    @NotNull
    Long serviceCatalogId;

}
