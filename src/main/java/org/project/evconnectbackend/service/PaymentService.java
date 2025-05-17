package org.project.evconnectbackend.service;

import org.project.evconnectbackend.dto.payment.PaymentRequestDTO;
import org.project.evconnectbackend.dto.payment.PaymentResponseDTO;

public interface PaymentService {
    
    /**
     * Process a payment for a transaction
     * 
     * @param email The email of the user making the payment
     * @param transactionId The ID of the transaction to pay for
     * @param paymentRequest The payment details
     * @return The payment response
     */
    PaymentResponseDTO processPayment(String email, Long transactionId, PaymentRequestDTO paymentRequest);
    
    /**
     * Get payment details for a transaction
     * 
     * @param email The email of the user requesting the payment details
     * @param transactionId The ID of the transaction
     * @return The payment response
     */
    PaymentResponseDTO getPaymentDetails(String email, Long transactionId);
    
    /**
     * Refund a payment
     * 
     * @param email The email of the user requesting the refund
     * @param transactionId The ID of the transaction to refund
     * @param refundRequest The refund details
     * @return The payment response
     */
    PaymentResponseDTO refundPayment(String email, Long transactionId, PaymentRequestDTO refundRequest);
} 