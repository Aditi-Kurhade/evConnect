package org.project.evconnectbackend.dto.station;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChargingStationResponse {
    private String id;
    private String name;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double pricePerHour;
    private String connectorType;
    private Double powerOutput;
    private List<String> images;
    private String ownerId;
    private boolean isAvailable;
    private Double rating;
    private Integer totalRatings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 