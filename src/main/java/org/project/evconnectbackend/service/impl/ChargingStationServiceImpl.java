package org.project.evconnectbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.station.ChargingStationResponse;
import org.project.evconnectbackend.dto.station.CreateChargingStationRequest;
import org.project.evconnectbackend.model.BookingStatus;
import org.project.evconnectbackend.model.ChargingStation;
import org.project.evconnectbackend.model.StationImage;
import org.project.evconnectbackend.model.User;
import org.project.evconnectbackend.repository.ChargingStationRepository;
import org.project.evconnectbackend.repository.UserRepository;
import org.project.evconnectbackend.service.ChargingStationService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargingStationServiceImpl implements ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;
    private final UserRepository userRepository;

    @Override
    public ChargingStationResponse createStation(String email, CreateChargingStationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChargingStation station = new ChargingStation();
        station.setName(request.getName());
        station.setAddress(request.getAddress());
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
        station.setConnectorType(request.getConnectorType());
        station.setChargingSpeed(request.getChargingSpeed());
        station.setPricePerUnit(request.getPricePerUnit());
        station.setAvailabilitySchedule(request.getAvailabilitySchedule());
        station.setDescription(request.getDescription());
        station.setUser(user);

        // Handle image uploads
        if (request.getImages() != null) {
            for (MultipartFile image : request.getImages()) {
                StationImage stationImage = new StationImage();
                stationImage.setChargingStation(station);
                station.getImages().add(stationImage);
            }
        }

        station = chargingStationRepository.save(station);
        return convertToResponse(station);
    }

    @Override
    public ChargingStationResponse getStation(Long id) {
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging station not found"));
        return convertToResponse(station);
    }

    @Override
    public List<ChargingStationResponse> getNearbyStations(double latitude, double longitude, double radius) {
        return chargingStationRepository.findNearbyStations(latitude, longitude, radius)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingStationResponse disableStation(String email, Long id) {
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging station not found"));

        if (!station.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to disable this station");
        }

        // Check if there are any active bookings
        boolean hasActiveBookings = station.getBookings().stream()
                .anyMatch(booking -> booking.getStatus() == BookingStatus.ACCEPTED);

        if (hasActiveBookings) {
            throw new RuntimeException("Cannot disable station with active bookings");
        }

        station.setEnabled(false);
        station = chargingStationRepository.save(station);
        return convertToResponse(station);
    }

    @Override
    public ChargingStationResponse enableStation(String email, Long id) {
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging station not found"));

        if (!station.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to enable this station");
        }

        station.setEnabled(true);
        station = chargingStationRepository.save(station);
        return convertToResponse(station);
    }

    @Override
    public List<ChargingStationResponse> getUserStations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return chargingStationRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingStationResponse updateStation(String email, Long id, CreateChargingStationRequest request) {
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging station not found"));

        // Check if the user is authorized to update this station
        if (!station.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to update this station");
        }

        // Update station fields
        station.setName(request.getName());
        station.setAddress(request.getAddress());
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
        station.setConnectorType(request.getConnectorType());
        station.setChargingSpeed(request.getChargingSpeed());
        station.setPricePerUnit(request.getPricePerUnit());
        station.setAvailabilitySchedule(request.getAvailabilitySchedule());
        station.setDescription(request.getDescription());

        // Handle image updates if provided
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            // Clear existing images
            station.getImages().clear();
            
            // Add new images
            for (MultipartFile image : request.getImages()) {
                StationImage stationImage = new StationImage();
                stationImage.setChargingStation(station);
                station.getImages().add(stationImage);
            }
        }

        station = chargingStationRepository.save(station);
        return convertToResponse(station);
    }

    private ChargingStationResponse convertToResponse(ChargingStation station) {
        ChargingStationResponse response = new ChargingStationResponse();
        response.setId(station.getId().toString());
        response.setName(station.getName());
        response.setDescription(station.getDescription());
        response.setAddress(station.getAddress());
        response.setLatitude(station.getLatitude());
        response.setLongitude(station.getLongitude());
        response.setPricePerHour(station.getPricePerUnit()); // Assuming price per unit is per hour
        response.setConnectorType(station.getConnectorType());
        response.setPowerOutput(Double.parseDouble(station.getChargingSpeed())); // Convert charging speed to power output
        response.setImages(station.getImages().stream()
                .map(StationImage::getImageUrl)
                .collect(Collectors.toList()));
        response.setOwnerId(station.getUser().getId().toString());
        response.setAvailable(station.isEnabled());
        response.setRating(station.getRating());
        response.setTotalRatings(station.getTotalRatings());
        response.setCreatedAt(station.getCreatedAt());
        response.setUpdatedAt(station.getUpdatedAt());
        return response;
    }
} 