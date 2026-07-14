package com.booking.controller;

import com.booking.model.dto.apartment.ApartmentRequest;
import com.booking.model.dto.apartment.ApartmentResponse;
import com.booking.model.dto.apartment.ApartmentSearchRequest;
import com.booking.model.service.ApartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apartments")
@RequiredArgsConstructor

public class ApartmentController {
    private final ApartmentService apartmentService;

    @GetMapping
    public ResponseEntity<Page<ApartmentResponse>>searchApartments(
            @Valid
            ApartmentSearchRequest searchRequest,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(apartmentService.searchApartments(searchRequest, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentResponse>getApartment(@PathVariable Long id){
        return ResponseEntity.ok(apartmentService.getApartmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<ApartmentResponse>createApartment(
            @Valid
            @RequestBody
            ApartmentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apartmentService.createApartment(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    public ResponseEntity<Void>deletApartment(@PathVariable Long id) {
        apartmentService.deleteApartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<Page<ApartmentResponse>>getMyApartments(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(apartmentService.getMyApartments(pageable));
    }
}











