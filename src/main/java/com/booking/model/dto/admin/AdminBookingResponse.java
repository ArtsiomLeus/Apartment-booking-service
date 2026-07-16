package com.booking.model.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminBookingResponse {
    private Long id;
    private String apartmentTitle;
    private String guestName;
    private String guestEmail;
    private String ownerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger totalPrice;
    private String status;
    private LocalDateTime createdAt;
}
