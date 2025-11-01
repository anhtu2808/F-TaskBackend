package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.booking.BookingPartnerResponse;
import com.anhtu.ftaskbackend.entity.BookingPartner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PartnerMapper.class})
public interface BookingPartnerMapper {

    BookingPartnerResponse toBookingPartnerResponse(BookingPartner bookingPartner);

}
