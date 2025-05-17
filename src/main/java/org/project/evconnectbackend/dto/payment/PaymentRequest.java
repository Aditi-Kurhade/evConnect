package org.project.evconnectbackend.dto.payment;

import lombok.Data;

@Data
public class PaymentRequest {
    private String cardNumber;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
} 