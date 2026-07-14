package com.booking.util;

import com.booking.model.dto.apartment.ApartmentRequest;
import com.booking.model.dto.Apartment;
import com.booking.model.dto.apartment.ApartmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ApartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Apartment toEntity(ApartmentRequest request);

    @Mapping(source = "owner.fullName", target = "ownerFullName")
    ApartmentResponse toResponse(Apartment apartment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntity(@MappingTarget Apartment apartment, ApartmentRequest request);
}


















