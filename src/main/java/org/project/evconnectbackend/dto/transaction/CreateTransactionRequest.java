package org.project.evconnectbackend.dto.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateTransactionRequest {
    @NotNull
    private Long bookingId;

    @NotNull
    @Positive
    private Double amount;

} 