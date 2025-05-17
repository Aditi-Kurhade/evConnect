package org.project.evconnectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.booking.BookingResponse;
import org.project.evconnectbackend.dto.booking.CreateBookingRequest;
import org.project.evconnectbackend.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(userDetails.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(userDetails.getUsername(), id));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getUserBookings(userDetails.getUsername()));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<BookingResponse>> getBookingRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getBookingRequests(userDetails.getUsername()));
    }

    @GetMapping("/{id}/amount")
    public ResponseEntity<Double> getBookingAmount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.calculateBookingAmount(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<BookingResponse> acceptBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.acceptBooking(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<BookingResponse> rejectBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.rejectBooking(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(userDetails.getUsername(), id));
    }
} 