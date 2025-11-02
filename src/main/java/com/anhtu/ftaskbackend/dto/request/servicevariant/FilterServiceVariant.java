package com.anhtu.ftaskbackend.dto.request.servicevariant;

import com.anhtu.ftaskbackend.entity.ServiceCatalog;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterServiceVariant {

    int page;
    int size;
    Long serviceCatalogId;
    String name;
    Integer durationHours;
    Double minPrice;
    Double maxPrice;
    Boolean isMultiPartner;
    Integer numberOfPartners;

}
