package com.booking.model.dto.booking;

import com.booking.model.dto.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private Long apartmentId;
    private String apartmentTitle;
    private String apartmentAddress;
    private String guestFullName;
    private String guestEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger totalPrice;
    private BookingStatus status;
    private Integer nightsCount;
}