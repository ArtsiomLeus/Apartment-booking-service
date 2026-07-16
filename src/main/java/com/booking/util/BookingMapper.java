package com.booking.util;

import com.booking.model.dto.booking.BookingRequest;
import com.booking.model.dto.booking.BookingResponse;
import com.booking.model.dto.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ApartmentMapper.class})
public interface BookingMapper {

    // BookingRequest → Booking (для создания)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "apartment", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", constant = "0")
    Booking toEntity(BookingRequest request);

    // Booking → BookingResponse
    @Mapping(source = "apartment.id", target = "apartmentId")
    @Mapping(source = "apartment.title", target = "apartmentTitle")
    @Mapping(source = "guest.id", target = "guestId")
    @Mapping(source = "guest.fullName", target = "guestName")
    @Mapping(source = "apartment.owner.fullName", target = "ownerName")
    BookingResponse toResponse(Booking booking);
}
