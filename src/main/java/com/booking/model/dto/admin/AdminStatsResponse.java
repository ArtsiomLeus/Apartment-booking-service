package com.booking.model.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigInteger;

@Data
@Builder
public class AdminStatsResponse {
    private Long totalUsers;
    private Long totalHosts;
    private Long totalGuests;
    private Long totalApartments;
    private Long totalBookings;
    private Long activeBookings;
    private Long pendingBookings;
    private Long cancelledBookings;
    private BigInteger totalRevenue;
}
