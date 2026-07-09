import com.booking.model.apartment.ApartmentRequest;
import com.booking.model.apartment.ApartmentResponse;
import com.booking.model.dto.Apartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStraregy;

@Mapper(componentModel = "spring",
        nullValueProrertyMappingStrategy = NullValueProrertyMappingStrategy.IGNORE)
public interface ApartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", constant = true)
    Apartment toEntity(ApartmentRequest request);

    @Mapping(source = "owner.fullName", target = "ownerFullName")
    ApartmentResponce toResponse(Apartment apartment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntity(@MappingTarget Apartment apartment, ApartmentRequest request);
}


















