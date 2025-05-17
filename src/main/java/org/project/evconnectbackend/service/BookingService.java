package org.project.evconnectbackend.service;


import org.project.evconnectbackend.dto.booking.BookingResponse;
import org.project.evconnectbackend.dto.booking.CreateBookingRequest;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(String email, CreateBookingRequest request);
    BookingResponse getBooking(String email, Long id);
    List<BookingResponse> getUserBookings(String email);
    List<BookingResponse> getBookingRequests(String email);
    BookingResponse acceptBooking(String email, Long id);
    BookingResponse rejectBooking(String email, Long id);
    BookingResponse cancelBooking(String email, Long id);
    Double calculateBookingAmount(String email, Long id);
} 