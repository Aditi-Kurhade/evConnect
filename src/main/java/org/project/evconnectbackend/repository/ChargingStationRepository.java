package org.project.evconnectbackend.repository;

import org.project.evconnectbackend.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    List<ChargingStation> findByUserId(Long userId);
    
    @Query(value = "SELECT * FROM charging_stations WHERE " +
            "is_enabled = true AND " +
            "ST_Distance_Sphere(point(longitude, latitude), point(:longitude, :latitude)) <= :radius", 
            nativeQuery = true)
    List<ChargingStation> findNearbyStations(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radiusInMeters);
} 