package org.project.evconnectbackend.dto.transaction;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Long bookingId;
    private Double amount;
    private String status;
    private String paymentId;
    private Double borrowerLatitude;
    private Double borrowerLongitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 