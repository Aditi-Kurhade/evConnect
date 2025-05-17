package org.project.evconnectbackend.service;



import org.project.evconnectbackend.dto.rating.CreateRatingRequest;
import org.project.evconnectbackend.dto.rating.RatingResponse;

import java.util.List;

public interface RatingService {
    RatingResponse createRating(String email, CreateRatingRequest request);
    RatingResponse getRating(String email, Long id);
    List<RatingResponse> getUserRatings(String email, Long userId);
    List<RatingResponse> getStationRatings(String email, Long stationId);
    List<RatingResponse> getMyRatings(String email);
} 