package org.project.evconnectbackend.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    
    private String paymentId;
    private String status;
    private Double amount;
    private String currency;
    private LocalDateTime timestamp;
    private String transactionReference;
    private String paymentMethod;
    
    // Additional response fields
    private String receiptUrl;
    private String errorMessage;
    private boolean success;
} 