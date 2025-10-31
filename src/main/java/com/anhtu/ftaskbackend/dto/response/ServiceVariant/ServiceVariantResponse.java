package com.anhtu.ftaskbackend.dto.response.ServiceVariant;

import com.anhtu.ftaskbackend.entity.ServiceCatalog;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceVariantResponse {

    Long id;
    String name;
    String description;
    Integer durationHours;
    Double pricePerVariant;
    Boolean isMultiPartner;
    Integer numberOfPartners;

}
