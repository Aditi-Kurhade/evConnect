package org.project.evconnectbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_station_id", nullable = false)
    private ChargingStation ratedStation;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer ratingValue;

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    @CreationTimestamp
    private LocalDateTime createdAt;
} 