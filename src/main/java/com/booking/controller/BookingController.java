package com.booking.controller;

import com.booking.model.dto.booking.BookingResponse;
import com.booking.model.dto.booking.BookingRequest;
import com.booking.model.dto.booking.BookingStatusUpdateRequest;
import com.booking.model.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<BookingResponse> createBooking(@Valid
                                                             @RequestBody BookingRequest request) {
        return
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(bookingService.createBooking(request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<Page<BookingResponse>> getMyBooking(@PageableDefault(size = 20,
            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return
                ResponseEntity.ok(bookingService.getMyApartmentsBookings(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Guest', 'HOST', 'ADMIN')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return
                ResponseEntity.noContent().build();
    }

    @GetMapping("/my-apartments")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<Page<BookingResponse>> getMyApartmentsBookings(@PageableDefault(
            size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return
                ResponseEntity.ok(bookingService.getMyApartmentsBookings(pageable));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusUpdateRequest request) {
        return
                ResponseEntity.ok(bookingService.updateBookingStatus(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        return
                ResponseEntity.ok(bookingService.getBookingBuId(id));
    }
}












