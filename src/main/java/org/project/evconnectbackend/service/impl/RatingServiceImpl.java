package org.project.evconnectbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.rating.CreateRatingRequest;
import org.project.evconnectbackend.dto.rating.RatingResponse;
import org.project.evconnectbackend.model.*;
import org.project.evconnectbackend.repository.BookingRepository;
import org.project.evconnectbackend.repository.ChargingStationRepository;
import org.project.evconnectbackend.repository.RatingRepository;
import org.project.evconnectbackend.repository.UserRepository;
import org.project.evconnectbackend.service.RatingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ChargingStationRepository chargingStationRepository;

    @Override
    public RatingResponse createRating(String email, CreateRatingRequest request) {
        User rater = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getBorrower().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to create rating for this booking");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot rate a booking that is not completed");
        }

        User ratedUser = userRepository.findById(request.getRatedUserId())
                .orElseThrow(() -> new RuntimeException("Rated user not found"));

        ChargingStation ratedStation = chargingStationRepository.findById(request.getRatedStationId())
                .orElseThrow(() -> new RuntimeException("Rated station not found"));

        if (ratingRepository.existsByBookingIdAndRaterId(booking.getId(), rater.getId())) {
            throw new RuntimeException("You have already rated this booking");
        }

        Rating rating = new Rating();
        rating.setBooking(booking);
        rating.setRater(rater);
        rating.setRatedUser(ratedUser);
        rating.setRatedStation(ratedStation);
        rating.setRatingValue(request.getRatingValue());
        rating.setReviewText(request.getReviewText());

        rating = ratingRepository.save(rating);
        return convertToResponse(rating);
    }

    @Override
    public RatingResponse getRating(String email, Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        // Check if the user is authorized to view this rating
        if (!rating.getRater().getEmail().equals(email) && 
            !rating.getRatedUser().getEmail().equals(email) && 
            !rating.getRatedStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to view this rating");
        }

        return convertToResponse(rating);
    }

    @Override
    public List<RatingResponse> getUserRatings(String email, Long userId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user is authorized to view these ratings
        if (!user.getId().equals(userId) && 
            !user.getCurrentRole().toString().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized to view these ratings");
        }

        return ratingRepository.findByRatedUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatingResponse> getStationRatings(String email, Long stationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Charging station not found"));

        // Check if the user is authorized to view these ratings
        if (!station.getUser().getEmail().equals(email) && 
            !user.getCurrentRole().toString().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized to view these ratings");
        }

        return ratingRepository.findByRatedStationId(stationId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatingResponse> getMyRatings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ratingRepository.findByRaterId(user.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private RatingResponse convertToResponse(Rating rating) {
        RatingResponse response = new RatingResponse();
        response.setId(rating.getId());
        response.setBookingId(rating.getBooking().getId());
        response.setRaterId(rating.getRater().getId());
        response.setRaterName(rating.getRater().getName());
        response.setRatedUserId(rating.getRatedUser().getId());
        response.setRatedUserName(rating.getRatedUser().getName());
        response.setRatedStationId(rating.getRatedStation().getId());
        response.setRatedStationAddress(rating.getRatedStation().getAddress());
        response.setRatingValue(rating.getRatingValue());
        response.setReviewText(rating.getReviewText());
        response.setCreatedAt(rating.getCreatedAt());
        return response;
    }
} 