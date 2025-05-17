package org.project.evconnectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.station.ChargingStationResponse;
import org.project.evconnectbackend.dto.station.CreateChargingStationRequest;
import org.project.evconnectbackend.service.ChargingStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/charging-stations")
@RequiredArgsConstructor
public class ChargingStationController {

    private final ChargingStationService chargingStationService;

    @PostMapping
    public ResponseEntity<ChargingStationResponse> createStation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute CreateChargingStationRequest request) {
        return ResponseEntity.ok(chargingStationService.createStation(userDetails.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingStationResponse> getStation(@PathVariable Long id) {
        return ResponseEntity.ok(chargingStationService.getStation(id));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<ChargingStationResponse>> getNearbyStations(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5000") double radius) {
        return ResponseEntity.ok(chargingStationService.getNearbyStations(latitude, longitude, radius));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<ChargingStationResponse> disableStation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(chargingStationService.disableStation(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<ChargingStationResponse> enableStation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(chargingStationService.enableStation(userDetails.getUsername(), id));
    }

    @GetMapping("/my-stations")
    public ResponseEntity<List<ChargingStationResponse>> getMyStations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chargingStationService.getUserStations(userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingStationResponse> updateStation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @ModelAttribute CreateChargingStationRequest request) {
        return ResponseEntity.ok(chargingStationService.updateStation(userDetails.getUsername(), id, request));
    }
} 