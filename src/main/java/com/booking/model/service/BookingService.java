package com.booking.model.service;

import com.booking.model.dto.booking.BookingResponse;
import com.booking.model.dto.booking.BookingRequest;
import com.booking.model.dto.booking.BookingStatusUpdateRequest;
import com.booking.model.dto.*;
import com.booking.exception.BookingConflictException;
import com.booking.exception.ResourceNotFoundException;
import com.booking.exception.UnauthorizedException;
import com.booking.util.BookingMapper;
import com.booking.repositories.ApartmentRepository;
import com.booking.repositories.BookingRepository;
import com.booking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    private static final List<BookingStatus> ACTIVE_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User guest = getCurrentUser();

        Apartment apartment = apartmentRepository.findById(request.getApartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found"));

        if (apartment.getOwner().getId().equals(guest.getId())) {
            throw new UnauthorizedException("You cannot book your own apartment");
        }

        if (!apartment.getIsActive()) {
            throw new IllegalArgumentException("Apartment is not available");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (request.getStartDate().equals(request.getEndDate())) {
            throw new IllegalArgumentException("Minimum stay is 1 night");
        }

        boolean conflict = bookingRepository.existsConflictingBooking(
                request.getApartmentId(),
                request.getStartDate(),
                request.getEndDate(),
                ACTIVE_STATUSES);

        if (conflict) {
            throw new BookingConflictException("Apartment is already " +
                    "booked for the sekected date");
        }

        long nights = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        BigInteger totalPrice = apartment.getPricePerNight().multiply(BigInteger.valueOf(nights));

        Booking booking = Booking.builder()
                .apartment(apartment)
                .guest(guest)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);
        return
                bookingMapper.toResponse(saved);
    }

    public BookingResponse getBookingBuId(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User currentUser = getCurrentUser();
        boolean isGuest = booking.getGuest().getId().equals(currentUser.getId());
        boolean isOwner = booking.getApartment().getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().equals(Role.ADMIN);

        if(!isGuest && !isOwner && !isAdmin) {
            throw new UnauthorizedException("You don't have permission to view this booking");
        }
        return
                bookingMapper.toResponse(booking);
    }

    public Page<BookingResponse> getMyBooking(Pageable pageable) {
        User guest = getCurrentUser();
        Page<Booking> bookings = bookingRepository.findByGuestId(guest.getId(), pageable);
        return
                bookings.map(bookingMapper::toResponse);
    }

    public Page<BookingResponse> getMyApartmentsBookings(Pageable pageable)  {
        User host = getCurrentUser();
        Page<Booking> bookings = bookingRepository.findByApartmentOwnerId(host.getId(), pageable);
        return
                bookings.map(bookingMapper::toResponse);
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long bookingId,
                                               BookingStatusUpdateRequest request) {
        User host = getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if(!booking.getApartment().getOwner().getId().equals(host.getId())) {
            throw new
                    UnauthorizedException("You are not the owner of this apartment");
        }

        if(booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Booking is already " + booking.getStatus());
        }

        BookingStatus newStatus = request.getStatus();

        if(newStatus != BookingStatus.CONFIRMED && newStatus != BookingStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid status, Use CONFIRMED or REJECTED");
        }

        booking.setStatus(newStatus);
        Booking updated = bookingRepository.save(booking);
        return
                bookingMapper.toResponse(updated);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        User currentUser = getCurrentUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        boolean isGuest = booking.getGuest().equals(currentUser.getId());
        boolean isOwner = booking.getApartment().getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().equals(Role.ADMIN);

        if(!isGuest && !isOwner && !isAdmin) {
            throw new
                    UnauthorizedException("You don't have permission to cancel this booking");
        }

        if(booking.getStatus() == BookingStatus.REJECTED ||
                booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel booking with status: "
                    + booking.getStatus());
        }

        if(booking.getStartDate().isBefore(LocalDate.now())) {
            throw new
                    IllegalStateException("Cannot cancel a booking that has already started");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Transactional
    public void completeExpiredBookings() {
        List<Booking> expired = bookingRepository.findExpiredBookings();
        for(Booking booking : expired) {
            booking.setStatus(BookingStatus.COMPLETED);
        }
        bookingRepository.saveAll(expired);
    }
}





















