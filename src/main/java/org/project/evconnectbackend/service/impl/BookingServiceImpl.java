package org.project.evconnectbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.booking.BookingResponse;
import org.project.evconnectbackend.dto.booking.CreateBookingRequest;
import org.project.evconnectbackend.model.*;
import org.project.evconnectbackend.repository.BookingRepository;
import org.project.evconnectbackend.repository.ChargingStationRepository;
import org.project.evconnectbackend.repository.UserRepository;
import org.project.evconnectbackend.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ChargingStationRepository chargingStationRepository;

    @Override
    public BookingResponse createBooking(String email, CreateBookingRequest request) {
        User borrower = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChargingStation station = chargingStationRepository.findById(request.getStationId())
                .orElseThrow(() -> new RuntimeException("Charging station not found"));

        if (!station.isEnabled()) {
            throw new RuntimeException("Charging station is not available");
        }

        Booking booking = new Booking();
        booking.setChargingStation(station);
        booking.setBorrower(borrower);
        booking.setBookingStartTime(request.getBookingStartTime());
        booking.setBookingEndTime(request.getBookingEndTime());
        booking.setStatus(BookingStatus.PENDING);

        booking = bookingRepository.save(booking);
        return convertToResponse(booking);
    }

    @Override
    public BookingResponse getBooking(String email, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getBorrower().getEmail().equals(email) && 
            !booking.getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to view this booking");
        }

        return convertToResponse(booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all bookings regardless of status
        List<Booking> bookings = bookingRepository.findByBorrowerId(user.getId());
        
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getBookingRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all bookings for the user's stations regardless of status
        List<Booking> bookings = bookingRepository.findByChargingStationUserId(user.getId());
        
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse acceptBooking(String email, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to accept this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in pending status");
        }

        // Calculate the duration in hours
        double hours = java.time.Duration.between(
            booking.getBookingStartTime(), 
            booking.getBookingEndTime()
        ).toMinutes() / 60.0;

        // Calculate the total amount
        double totalAmount = hours * booking.getChargingStation().getPricePerUnit();

        // Create a new transaction
        Transaction transaction = new Transaction();
        transaction.setBooking(booking);
        transaction.setAmount(totalAmount);
        transaction.setStatus(TransactionStatus.PENDING);

        // Update booking status and save transaction
        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setTransaction(transaction);
        booking = bookingRepository.save(booking);
        
        return convertToResponse(booking);
    }

    @Override
    public BookingResponse rejectBooking(String email, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to reject this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in pending status");
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        return convertToResponse(booking);
    }

    @Override
    public BookingResponse cancelBooking(String email, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getBorrower().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING && 
            booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        return convertToResponse(booking);
    }

    @Override
    public Double calculateBookingAmount(String email, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if user is authorized to view this booking
        if (!booking.getBorrower().getEmail().equals(email) &&
            !booking.getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to view this booking");
        }

        // Calculate the duration in hours
        double hours = java.time.Duration.between(
            booking.getBookingStartTime(), 
            booking.getBookingEndTime()
        ).toMinutes() / 60.0;

        // Calculate and return the total amount
        return hours * booking.getChargingStation().getPricePerUnit();
    }

    private BookingResponse convertToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setStationId(booking.getChargingStation().getId());
        response.setStationAddress(booking.getChargingStation().getAddress());
        response.setStationPricePerUnit(booking.getChargingStation().getPricePerUnit());
        response.setBorrowerId(booking.getBorrower().getId());
        response.setBorrowerName(booking.getBorrower().getName());
        response.setBookingStartTime(booking.getBookingStartTime());
        response.setBookingEndTime(booking.getBookingEndTime());
        response.setStatus(booking.getStatus().toString());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        return response;
    }
} 