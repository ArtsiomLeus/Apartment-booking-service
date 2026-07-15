package com.booking.model.service;

import com.booking.model.dto.booking.BookingRequest;
import com.booking.model.dto.*;
import com.booking.exception.BookingConflictException;
import com.booking.repositories.BookingRepository;
import com.booking.repositories.ApartmentRepository;
import com.booking.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User guest;
    private User host;
    private Apartment apartment;
    private BookingRequest validRequest;

    @BeforeEach
    void setUp() {
        guest = User.builder().id(1L).email("guest@test.com").role(Role.GUEST).build();
        host = User.builder().id(2L).email("host@test.com").role(Role.HOST).build();

        apartment = Apartment.builder()
                .id(100L)
                .owner(host)
                .title("Test Apartment")
                .pricePerNight(BigInteger.valueOf(100))
                .isActive(true)
                .build();

        validRequest = new BookingRequest();
        validRequest.setApartmentId(100L);
        validRequest.setStartDate(LocalDate.now().plusDays(5));
        validRequest.setEndDate(LocalDate.now().plusDays(7));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "guest@test.com", "password")
        );
    }

    @Test
    void createBooking_ShouldSucceed_WhenNoConflict() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(guest));
        when(apartmentRepository.findById(100L)).thenReturn(Optional.of(apartment));
        when(bookingRepository.existsConflictingBooking(anyLong(), any(), any(), anyList()))
                .thenReturn(false);
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var response = bookingService.createBooking(validRequest);

        assertNotNull(response);

        assertEquals(BookingStatus.PENDING, response.getStatus());

        assertEquals(BigInteger.valueOf(200), response.getTotalPrice());
    }

    @Test
    void createBooking_ShouldThrowConflict_WhenDatesOverlap() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(guest));
        when(apartmentRepository.findById(100L)).thenReturn(Optional.of(apartment));
        when(bookingRepository.existsConflictingBooking(anyLong(), any(), any(), anyList()))
                .thenReturn(true);

        assertThrows(BookingConflictException.class, () ->
                bookingService.createBooking(validRequest));
    }

    @Test
    void cancelBooking_ShouldSucceed_WhenGuestCancelsPending()  {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(guest));

        Booking booking = Booking.builder()
                .id(1L)
                .guest(guest)
                .apartment(apartment)
                .startDate(LocalDate.now().plusDays(10))
                .status(BookingStatus.PENDING)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        assertDoesNotThrow(() -> bookingService.cancelBooking(1L));
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }
}













