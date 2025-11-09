package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.Customer.AddressResponse;
import com.anhtu.ftaskbackend.dto.response.Customer.CustomerResponse;
import com.anhtu.ftaskbackend.dto.response.ServiceVariant.ServiceVariantResponse;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.entity.Address;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.Customer;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, AddressMapper.class, ServiceVariantMapper.class, BookingPartnerMapper.class})
public interface BookingMapper {

    @Mapping(target = "numberOfJoinedPartner",
            expression = "java(booking.getPartners() == null ? 0 : booking.getPartners().size())")
    BookingResponse toBookingResponse(Booking booking);

}
