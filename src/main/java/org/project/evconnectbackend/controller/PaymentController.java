package org.project.evconnectbackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.evconnectbackend.dto.payment.PaymentRequestDTO;
import org.project.evconnectbackend.dto.payment.PaymentResponseDTO;
import org.project.evconnectbackend.service.PaymentService;
import org.project.evconnectbackend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final TransactionService transactionService;

    @PostMapping("/{transactionId}/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @PathVariable Long transactionId,
            @RequestBody PaymentRequestDTO paymentRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Processing payment for transaction ID: {} by user: {}", transactionId, userDetails.getUsername());
        
        PaymentResponseDTO response = paymentService.processPayment(userDetails.getUsername(), transactionId, paymentRequest);
        log.debug("Payment processed successfully for transaction ID: {}", transactionId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentDetails(
            @PathVariable Long transactionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Retrieving payment details for transaction ID: {} by user: {}", transactionId, userDetails.getUsername());
        
        PaymentResponseDTO details = paymentService.getPaymentDetails(userDetails.getUsername(), transactionId);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<PaymentResponseDTO> refundPayment(
            @PathVariable Long transactionId,
            @RequestBody PaymentRequestDTO refundRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Processing refund for transaction ID: {} by user: {}", transactionId, userDetails.getUsername());
        
        PaymentResponseDTO response = paymentService.refundPayment(userDetails.getUsername(), transactionId, refundRequest);
        log.debug("Refund processed successfully for transaction ID: {}", transactionId);
        
        return ResponseEntity.ok(response);
    }
} 