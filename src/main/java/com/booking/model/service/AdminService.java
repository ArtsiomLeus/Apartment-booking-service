package com.booking.model.service;

import com.booking.model.dto.admin.AdminUserResponse;
import com.booking.model.dto.admin.AdminBookingResponse;
import com.booking.model.dto.admin.AdminStatsResponse;
import com.booking.model.dto.Booking;
import com.booking.model.dto.BookingStatus;
import com.booking.model.dto.Role;
import com.booking.model.dto.User;
import com.booking.exception.ResourceNotFoundException;
import com.booking.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ApartmentRepository apartmentRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toAdminUserResponse);
    }

    public AdminUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return toAdminUserResponse(user);
    }

    @Transactional
    public AdminUserResponse changeUserRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        try {
            Role newRole = Role.valueOf(roleName.toUpperCase());
            user.setRole(newRole);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + roleName);
        }

        return toAdminUserResponse(user);
    }

    @Transactional
    public void blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void unblockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setIsActive(true);
        userRepository.save(user);
    }

    public Page<AdminBookingResponse> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(this::toAdminBookingResponse);
    }

    public AdminBookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        return toAdminBookingResponse(booking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking not found: " + id);
        }
        bookingRepository.deleteById(id);
    }

    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalHosts = userRepository.countByRole(Role.HOST);
        long totalGuests = userRepository.countByRole(Role.GUEST);
        long totalApartments = apartmentRepository.count();
        long totalBookings = bookingRepository.count();
        long activeBookings = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        long cancelledBookings = bookingRepository.countByStatus(BookingStatus.CANCELLED);

        BigInteger totalRevenue = bookingRepository.sumTotalPriceByStatus(BookingStatus.CONFIRMED);
        if (totalRevenue == null) totalRevenue = BigInteger.ZERO;

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalHosts(totalHosts)
                .totalGuests(totalGuests)
                .totalApartments(totalApartments)
                .totalBookings(totalBookings)
                .activeBookings(activeBookings)
                .pendingBookings(pendingBookings)
                .cancelledBookings(cancelledBookings)
                .totalRevenue(totalRevenue)
                .build();
    }

    public Map<String, Object> getDailyStats(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        List<Object[]> results = bookingRepository.getDailyStats(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> dailyData = results.stream().map(row -> {
            Map<String, Object> day = new HashMap<>();
            day.put("date", ((LocalDateTime) row[0]).format(DateTimeFormatter.ISO_LOCAL_DATE));
            day.put("bookings", row[1]);
            day.put("revenue", row[2]);
            return day;
        }).collect(Collectors.toList());

        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("data", dailyData);

        return response;
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        long bookingsCount = bookingRepository.countByGuestId(user.getId());

        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .bookingsCount(bookingsCount)
                .build();
    }

    private AdminBookingResponse toAdminBookingResponse(Booking booking) {
        return AdminBookingResponse.builder()
                .id(booking.getId())
                .apartmentTitle(booking.getApartment().getTitle())
                .guestName(booking.getGuest().getFullName())
                .guestEmail(booking.getGuest().getEmail())
                .ownerName(booking.getApartment().getOwner().getFullName())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}













