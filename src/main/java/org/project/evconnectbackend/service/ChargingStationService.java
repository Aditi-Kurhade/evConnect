package org.project.evconnectbackend.service;



import org.project.evconnectbackend.dto.station.ChargingStationResponse;
import org.project.evconnectbackend.dto.station.CreateChargingStationRequest;
import java.util.List;

public interface ChargingStationService {
    ChargingStationResponse createStation(String email, CreateChargingStationRequest request);
    ChargingStationResponse getStation(Long id);
    List<ChargingStationResponse> getNearbyStations(double latitude, double longitude, double radius);
    ChargingStationResponse disableStation(String email, Long id);
    ChargingStationResponse enableStation(String email, Long id);
    List<ChargingStationResponse> getUserStations(String email);
    ChargingStationResponse updateStation(String email, Long id, CreateChargingStationRequest request);
} 