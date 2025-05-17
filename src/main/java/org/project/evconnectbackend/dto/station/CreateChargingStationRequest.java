package org.project.evconnectbackend.dto.station;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateChargingStationRequest {
    @NotBlank
    private String name; // Add this line

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

    private String availabilitySchedule;

    private String description;

    private List<MultipartFile> images;
} 