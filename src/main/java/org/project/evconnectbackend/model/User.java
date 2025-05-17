package org.project.evconnectbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$")
    private String phoneNumber;

    @NotBlank
    @ToString.Exclude
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    private UserType currentRole;

    private boolean isEnabled = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Set<ChargingStation> chargingStations = new HashSet<>();

    @OneToMany(mappedBy = "borrower", fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "ratedUser", fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Set<Rating> receivedRatings = new HashSet<>();

    @OneToMany(mappedBy = "rater", fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Set<Rating> givenRatings = new HashSet<>();
} 