package org.project.evconnectbackend.repository;


import org.project.evconnectbackend.model.Booking;
import org.project.evconnectbackend.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBorrowerId(Long borrowerId);
    List<Booking> findByChargingStationUserId(Long lenderId);
    List<Booking> findByBorrowerIdAndStatus(Long borrowerId, BookingStatus status);
    List<Booking> findByChargingStationUserIdAndStatus(Long lenderId, BookingStatus status);
} 