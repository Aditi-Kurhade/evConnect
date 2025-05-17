package org.project.evconnectbackend.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    
    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private String paymentMethod;
    
    @Builder.Default
    private String currency = "USD";
    
    private String description;
    
    // Additional payment-specific fields can be added here
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    private String billingZip;
} 