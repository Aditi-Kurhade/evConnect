package org.project.evconnectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.transaction.CreateTransactionRequest;
import org.project.evconnectbackend.dto.transaction.TransactionResponse;
import org.project.evconnectbackend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateTransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(userDetails.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransaction(userDetails.getUsername(), id));
    }

    @GetMapping("/my-transactions")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getUserTransactions(userDetails.getUsername()));
    }

    @PostMapping("/{id}/confirm-location")
    public ResponseEntity<TransactionResponse> confirmLocation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam double latitude,
            @RequestParam double longitude) {
        return ResponseEntity.ok(transactionService.confirmLocation(userDetails.getUsername(), id, latitude, longitude));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<TransactionResponse> processPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.processPayment(userDetails.getUsername(), id));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<TransactionResponse> getTransactionByBookingId(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(transactionService.getTransactionByBookingId(userDetails.getUsername(), bookingId));
    }
} 