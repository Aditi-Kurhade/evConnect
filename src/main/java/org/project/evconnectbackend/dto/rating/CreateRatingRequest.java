package org.project.evconnectbackend.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRatingRequest {
    @NotNull
    private Long bookingId;

    @NotNull
    private Long ratedUserId;

    @NotNull
    private Long ratedStationId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer ratingValue;

    private String reviewText;
} 