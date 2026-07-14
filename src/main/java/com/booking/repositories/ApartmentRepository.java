package com.booking.repositories;

import com.booking.model.dto.Apartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    Page<Apartment> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Apartment> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT a FROM Apartment a WHERE " +
            "(:city IS NULL OF LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
            "AND (:minGuests IS NULL OR a.maxGuests >= :minGuests)" +
            "AND (:minPrice IS NULL OR a.pricePerNight >= :minPrice)" +
            "AND (:maxPrice IS NULL OR a.pricePerNight <= :maxPrice)" +
            "AND a.isActive = true")
    Page<Apartment> searchApartments(
            @Param("city") String city,
            @Param("minGuests") Integer minGuests,
            @Param("minPrice") BigInteger minPrice,
            @Param("maxPrice") BigInteger maxPrice,
            Pageable pageable);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}

















