package org.project.evconnectbackend.repository;


import org.project.evconnectbackend.model.Transaction;
import org.project.evconnectbackend.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBookingBorrowerId(Long borrowerId);
    List<Transaction> findByBookingChargingStationUserId(Long lenderId);
    List<Transaction> findByStatus(TransactionStatus status);
    java.util.Optional<Transaction> findByBookingId(Long bookingId);
} 