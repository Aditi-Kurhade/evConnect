package org.project.evconnectbackend.service;

import org.project.evconnectbackend.dto.transaction.CreateTransactionRequest;
import org.project.evconnectbackend.dto.transaction.TransactionResponse;
import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(String email, CreateTransactionRequest request);
    TransactionResponse getTransaction(String email, Long id);
    List<TransactionResponse> getUserTransactions(String email);
    TransactionResponse confirmLocation(String email, Long id, double latitude, double longitude);
    TransactionResponse processPayment(String email, Long id);
    TransactionResponse getTransactionByBookingId(String email, Long bookingId);
} 