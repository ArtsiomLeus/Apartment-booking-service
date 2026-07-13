import com.booking.model.apartment.ApartmentRequest;
import com.booking.model.dto.auth.Role;
import com.booking.model.dto.auth.User;
import com.booking.exception.UnauthorizedException;
import com.booking.model.dto.repositories.ApartmentRepository;
import com.booking.model.dto.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticatinToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMathers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceTest{
    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApartmentService apartmentService;

    private User testUser;
    private ApartmentRequest validRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("host@email.com")
                .fullName("Test")
                .role(Role.HOST)
                .build();

        validRequest = new ApartmentRequest();
        validRequest.setTitle("Studio");
        validRequest.setDescription("Apartment in center");
        validRequest.setPricePerNight(new BigInteger ("130"));
        validRequest.setAddress("Rose 23");
        validRequest.setCity("Minsk");
        validRequest.setMaxGuests(3);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePsswordAuthenticationToken("host@email.com", "password")
        );
    }

    @Test
    void createApartment_ShouldSucceed_WhenUserIsHost() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(apartmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = apartmentService.createApartment(validRequest);
        assertNotNull(response);
        assertEquals(validRequest.getTitle(), response.getTitle());
        assertEquals(testUser.getFullName(), response.getOwnerFullName());
    }
    @Test
    void createApartment_ShouldThrowExceprion_WhenUserIsGuest() {
        testUser.setRole(Role.GUEST);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(UnauthorizedException.class, () -> apartmentService.createApartment(validRequest));
    }
}
















