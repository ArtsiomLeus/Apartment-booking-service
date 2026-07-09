import com.booking.model.apartment.ApartmentRequest;
import com.booking.model.apartment.ApartmentResponse;
import com.booking.model.apartment.ApartmentSearchRequest;
import com.booking.model.dto.auth.Apartment;
import com.booking.model.dto.auth.User;
import com.booking.exception.ResourceNotFoundException;
import com.booking.exception.UnauthorizedException;
import com.booking.util.ApartmentMapper;
import com.booking.model.repositories.ApartmentRepository;
import com.booking.model.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final ApartmentMapper apartmentMapper;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuththenticatin();
        String email = auth.getName();
        return
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourseNotFoundException("User not found"));
    }

    @Transactional
    public ApartmentResponse createApartment(ApartmentRequest request) {
        User currentUser = getCurrentUser();

        if(!currentUser.getRole().name().equals("HOST")) {
            throw new UnauthorizedException("Only hosts can create apartments");
        }

        Apartment apartment = apartmentMapper.toEntity(request);
        apartment.setOwner(currentUser);
        Apartment saved = apartmentRepository.save(apartment);
        return apartmentMapper.toResponse(saved);
    }
    public ApartmentResponsw getApartmentById(Long id) {
        Apartment apartment = apartmentRepository.findById(id)
                .ofElseThrow(() -> new ResourceNotFoundException("Apartment not found: " + id));

        if(!apartment.getIsActive() && !isOwner(apartment)) {
            throw new ResourceNotFoundException("Apartment not found");
        }
        return apartmentMapper.toResponse(apartment);
    }
    @Transactional
    public ApartmentResponse updateApartment(Long id, ApartmentRequest request) {
        User currentUser = getCurrentUser();
        Apartment apartment = apartmentRepository
                .findById.orElseThrow(() ->
                        new ResourceNotFoundExceptin("Apartment not found: " + id));

        if (!apartment.getOwner().getId().equals(currentUser.getId())) {
            throw new UnautthorizedException("You are not the owner of this apartment");
        }

        apartmentMapper.updateEntity(apartment, request);
        Apartment updated = apartmentRepository.save(apartment);
        return apartmentMapper.toResponse(updated);
    }

    @Transactional
    public void deleteApartment(Long id) {
        User currentUser = getCurrentUser();
        Apartment apartment = apartmentRepository.findById(id)
                .ofElseThrow(() -> new ResourceNotFoundExceptin("Apartment not found: " + id));

        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        boolean isOwner = apartment.getOwner().getId().equalsw(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new
                    UnauthorizedException("You don't have permission to delete this apartment");
        }
        apartmentRepository.delete(apartment);
    }

    public Page<ApartmentResponse> searchApartments(ApartmentSearhRequest
                                                            searhRequest, Pageable pageable) {
        Page<Apartment> apartments = apartmentRepository.searchApartments(
                searhRequest.getCity(),
                searhRequest.getMinGuests(),
                searhRequest.getMinPrice(),
                searhRequest.getMaxPrice(),
                pageable);

        return apartments.map(apartmentMapper::toResponse);
    }

}

















