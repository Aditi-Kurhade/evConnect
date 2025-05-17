package org.project.evconnectbackend.dto.rating;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponse {
    private Long id;
    private Long bookingId;
    private Long raterId;
    private String raterName;
    private Long ratedUserId;
    private String ratedUserName;
    private Long ratedStationId;
    private String ratedStationAddress;
    private Integer ratingValue;
    private String reviewText;
    private LocalDateTime createdAt;
} 