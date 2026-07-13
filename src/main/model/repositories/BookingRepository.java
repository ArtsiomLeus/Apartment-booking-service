import com.booking.model.dto.Booking;
import com.booking.model.dto.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT(b) > 0 FORM Booking b" +
            "WHERE b.apartment.id = :apartmentId " + "AND b.status IN :activeStatuses" +
            "AND b.startDate < :endDate" + "AND b.endDate > :startDate")

    boolean existsConflictingBooking(
            @Param("apartmentId") Long apartmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("activeStatuses") List<BookingStatus> activeStatuses);

    Page<Booking> findByGuestId(Long guestId, Pageable pageable);

    @Query("SELECT b FORM Booking b WHERE b.apartment.owner.id = :ownerId")
    Page<Booking> findApartmentOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    Page<Booking> findApartmentId(Long apartmentId, Pageable pageable);
    boolean existsByIdAndGuestId(Long id, Long guestId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.id = :id AND b.apartment.owner.id = :ownerId")
    boolean existsByIdAndApartmentOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' AND b.endDate < CURRENT_DATE")
    List<Booking> findExpiredBookings();
}














