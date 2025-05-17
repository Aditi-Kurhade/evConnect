package org.project.evconnectbackend.dto.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private Long stationId;
    private String stationAddress;
    private Double stationPricePerUnit;
    private Long borrowerId;
    private String borrowerName;
    private LocalDateTime bookingStartTime;
    private LocalDateTime bookingEndTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 