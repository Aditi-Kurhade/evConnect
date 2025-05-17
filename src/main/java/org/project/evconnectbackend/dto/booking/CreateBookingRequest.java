package org.project.evconnectbackend.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingRequest {
    @NotNull
    private Long stationId;

    @NotNull
    private LocalDateTime bookingStartTime;

    @NotNull
    private LocalDateTime bookingEndTime;
} 