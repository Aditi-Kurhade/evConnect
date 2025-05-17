package org.project.evconnectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.rating.CreateRatingRequest;
import org.project.evconnectbackend.dto.rating.RatingResponse;
import org.project.evconnectbackend.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateRatingRequest request) {
        return ResponseEntity.ok(ratingService.createRating(userDetails.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingResponse> getRating(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getRating(userDetails.getUsername(), id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingResponse>> getUserRatings(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getUserRatings(userDetails.getUsername(), userId));
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<RatingResponse>> getStationRatings(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long stationId) {
        return ResponseEntity.ok(ratingService.getStationRatings(userDetails.getUsername(), stationId));
    }

    @GetMapping("/my-ratings")
    public ResponseEntity<List<RatingResponse>> getMyRatings(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ratingService.getMyRatings(userDetails.getUsername()));
    }
} 