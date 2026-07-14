package com.booking.model.dto.booking;

import com.booking.model.dto.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private BookingStatus status;
}










