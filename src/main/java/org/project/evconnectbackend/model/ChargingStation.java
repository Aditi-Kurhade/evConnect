package org.project.evconnectbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "charging_stations")
public class ChargingStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotBlank
    private String connectorType;

    @NotBlank
    private String chargingSpeed;

    @Positive
    private Double pricePerUnit;

    @Column(columnDefinition = "TEXT")
    private String availabilitySchedule;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean isEnabled = true;

    @Column(columnDefinition = "DECIMAL(3,2) DEFAULT 0.0")
    private Double rating = 0.0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalRatings = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "chargingStation", cascade = CascadeType.ALL)
    private List<StationImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "chargingStation", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "ratedStation", cascade = CascadeType.ALL)
    private List<Rating> ratings = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
} 