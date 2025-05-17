package org.project.evconnectbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.payment.PaymentRequestDTO;
import org.project.evconnectbackend.dto.payment.PaymentResponseDTO;
import org.project.evconnectbackend.model.Transaction;
import org.project.evconnectbackend.model.TransactionStatus;
import org.project.evconnectbackend.model.User;
import org.project.evconnectbackend.repository.TransactionRepository;
import org.project.evconnectbackend.repository.UserRepository;
import org.project.evconnectbackend.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public PaymentResponseDTO processPayment(String email, Long transactionId, PaymentRequestDTO paymentRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getBooking().getBorrower().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to process payment for this transaction");
        }

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Transaction is not in pending status");
        }

        // In a real application, this would integrate with a payment gateway like Stripe
        // For now, we'll simulate a successful payment
        try {
            // Validate card details (in a real app, this would be done by the payment gateway)
            validateCardDetails(paymentRequest);

            // Process payment (in a real app, this would call the payment gateway API)
            String paymentId = UUID.randomUUID().toString();
            String cardLastFourDigits = paymentRequest.getCardNumber().substring(paymentRequest.getCardNumber().length() - 4);

            // Update transaction status
            transaction.setPaymentId(paymentId);
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);

            // Create payment response
            return PaymentResponseDTO.builder()
                    .paymentId(paymentId)
                    .status("SUCCESS")
                    .amount(transaction.getAmount())
                    .currency("USD")
                    .timestamp(LocalDateTime.now())
                    .transactionReference(transaction.getId().toString())
                    .paymentMethod("CARD")
                    .success(true)
                    .build();
        } catch (Exception e) {
            // Create failed payment response
            return PaymentResponseDTO.builder()
                    .status("FAILED")
                    .amount(transaction.getAmount())
                    .currency("USD")
                    .timestamp(LocalDateTime.now())
                    .transactionReference(transaction.getId().toString())
                    .errorMessage(e.getMessage())
                    .success(false)
                    .build();
        }
    }

    @Override
    public PaymentResponseDTO getPaymentDetails(String email, Long transactionId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Check if the user is authorized to view payment details
        if (!transaction.getBooking().getBorrower().getEmail().equals(email) && 
            !transaction.getBooking().getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to view payment details for this transaction");
        }

        // Create payment response
        return PaymentResponseDTO.builder()
                .paymentId(transaction.getPaymentId())
                .status(transaction.getStatus().toString())
                .amount(transaction.getAmount())
                .currency("USD")
                .timestamp(transaction.getUpdatedAt())
                .transactionReference(transaction.getId().toString())
                .paymentMethod("CARD")
                .success(transaction.getStatus() == TransactionStatus.COMPLETED)
                .build();
    }

    @Override
    public PaymentResponseDTO refundPayment(String email, Long transactionId, PaymentRequestDTO refundRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Check if the user is authorized to refund the payment
        if (!transaction.getBooking().getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to refund this payment");
        }

        if (transaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new RuntimeException("Transaction is not in completed status");
        }

        // In a real application, this would call the payment gateway's refund API
        // For now, we'll simulate a successful refund
        try {
            // Process refund (in a real app, this would call the payment gateway API)
            String refundId = UUID.randomUUID().toString();

            // Update transaction status
            transaction.setStatus(TransactionStatus.REFUNDED);
            transaction = transactionRepository.save(transaction);

            // Create payment response
            return PaymentResponseDTO.builder()
                    .paymentId(refundId)
                    .status("REFUNDED")
                    .amount(transaction.getAmount())
                    .currency("USD")
                    .timestamp(LocalDateTime.now())
                    .transactionReference(transaction.getId().toString())
                    .paymentMethod("CARD")
                    .success(true)
                    .build();
        } catch (Exception e) {
            // Create failed refund response
            return PaymentResponseDTO.builder()
                    .status("REFUND_FAILED")
                    .amount(transaction.getAmount())
                    .currency("USD")
                    .timestamp(LocalDateTime.now())
                    .transactionReference(transaction.getId().toString())
                    .errorMessage(e.getMessage())
                    .success(false)
                    .build();
        }
    }

    private void validateCardDetails(PaymentRequestDTO paymentRequest) {
        // In a real application, this would be done by the payment gateway
        // For now, we'll do some basic validation
        if (paymentRequest.getCardNumber() == null || paymentRequest.getCardNumber().length() < 16) {
            throw new RuntimeException("Invalid card number");
        }

        if (paymentRequest.getExpiryMonth() == null || paymentRequest.getExpiryMonth().isEmpty()) {
            throw new RuntimeException("Expiry month is required");
        }

        if (paymentRequest.getExpiryYear() == null || paymentRequest.getExpiryYear().isEmpty()) {
            throw new RuntimeException("Expiry year is required");
        }

        if (paymentRequest.getCvv() == null || paymentRequest.getCvv().length() < 3) {
            throw new RuntimeException("Invalid CVV");
        }
    }
} 