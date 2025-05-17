package org.project.evconnectbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.transaction.CreateTransactionRequest;
import org.project.evconnectbackend.dto.transaction.TransactionResponse;
import org.project.evconnectbackend.exception.TransactionExistsException;
import org.project.evconnectbackend.model.*;
import org.project.evconnectbackend.repository.BookingRepository;
import org.project.evconnectbackend.repository.TransactionRepository;
import org.project.evconnectbackend.repository.UserRepository;
import org.project.evconnectbackend.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public TransactionResponse createTransaction(String email, CreateTransactionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if the user is the lender (station owner)
        if (!booking.getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to create transaction for this booking");
        }

        if (booking.getTransaction() != null) {
            throw new TransactionExistsException("Transaction already exists for this booking", booking.getId());
        }

        Transaction transaction = new Transaction();
        transaction.setBooking(booking);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);

        transaction = transactionRepository.save(transaction);
        return convertToResponse(transaction);
    }

    @Override
    public TransactionResponse getTransaction(String email, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getBooking().getBorrower().getEmail().equals(email) && 
            !transaction.getBooking().getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to view this transaction");
        }

        return convertToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getUserTransactions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions;
        if (user.getCurrentRole().toString().equals("BORROWER")) {
            transactions = transactionRepository.findByBookingBorrowerId(user.getId());
        } else {
            transactions = transactionRepository.findByBookingChargingStationUserId(user.getId());
        }

        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse confirmLocation(String email, Long id, double latitude, double longitude) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getBooking().getBorrower().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to confirm location for this transaction");
        }

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Transaction is not in pending status");
        }

        // Update borrower's location
        transaction.setBorrowerLatitude(latitude);
        transaction.setBorrowerLongitude(longitude);

        // Check if borrower is within acceptable range of the charging station
        double stationLatitude = transaction.getBooking().getChargingStation().getLatitude();
        double stationLongitude = transaction.getBooking().getChargingStation().getLongitude();
        
        double distance = calculateDistance(latitude, longitude, stationLatitude, stationLongitude);
        
        if (distance > 0.1) { // 100 meters radius
            throw new RuntimeException("You are not at the charging station location");
        }

        transaction = transactionRepository.save(transaction);
        return convertToResponse(transaction);
    }

    @Override
    public TransactionResponse processPayment(String email, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getBooking().getBorrower().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to process payment for this transaction");
        }

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Transaction is not in pending status");
        }

        // In a real application, this would integrate with a payment gateway
        // For now, we'll simulate a successful payment
        String paymentId = UUID.randomUUID().toString();
        
        transaction.setPaymentId(paymentId);
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        // Update booking status to completed
        Booking booking = transaction.getBooking();
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        transaction = transactionRepository.save(transaction);
        return convertToResponse(transaction);
    }

    @Override
    public TransactionResponse getTransactionByBookingId(String email, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Verify user is authorized to access this booking's transaction
        if (!booking.getBorrower().getEmail().equals(email) && 
            !booking.getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to view this booking's transaction");
        }
        
        Transaction transaction = transactionRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Transaction not found for this booking"));
        
        return convertToResponse(transaction);
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setBookingId(transaction.getBooking().getId());
        response.setAmount(transaction.getAmount());
        response.setStatus(transaction.getStatus().toString());
        response.setPaymentId(transaction.getPaymentId());
        response.setBorrowerLatitude(transaction.getBorrowerLatitude());
        response.setBorrowerLongitude(transaction.getBorrowerLongitude());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        return response;
    }

    // Haversine formula to calculate distance between two points on Earth
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance; // Distance in km
    }
} 