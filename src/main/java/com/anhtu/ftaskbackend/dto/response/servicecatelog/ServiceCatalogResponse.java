package com.anhtu.ftaskbackend.dto.response.servicecatelog;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCatalogResponse {

    Long id;
    String name;
    String description;
    String imageUrl;
    Double platformFeePercent;
    Boolean isActive;
}