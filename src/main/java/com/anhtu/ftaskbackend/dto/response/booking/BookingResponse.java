package com.anhtu.ftaskbackend.dto.response.booking;

import com.anhtu.ftaskbackend.dto.response.Customer.AddressResponse;
import com.anhtu.ftaskbackend.dto.response.Customer.CustomerResponse;
import com.anhtu.ftaskbackend.dto.response.ServiceVariant.ServiceVariantResponse;
import com.anhtu.ftaskbackend.entity.Address;
import com.anhtu.ftaskbackend.entity.Customer;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {

    Long id;
    LocalDateTime startAt;
    Double totalPrice;
    String customerNote;
    Integer requiredPartners;
    BookingStatus status;
    LocalDateTime completedAt;
    CustomerResponse customer;
    ServiceVariantResponse variant;
    AddressResponse address;

}
