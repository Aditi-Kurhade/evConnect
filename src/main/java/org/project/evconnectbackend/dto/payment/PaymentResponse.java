package org.project.evconnectbackend.dto.payment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long transactionId;
    private String paymentId;
    private String status;
    private double amount;
    private String currency;
    private LocalDateTime paymentDate;
    private String cardLastFourDigits;
    private String errorMessage;
} 