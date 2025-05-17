package org.project.evconnectbackend.repository;

import org.project.evconnectbackend.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRatedUserId(Long userId);
    List<Rating> findByRatedStationId(Long stationId);
    List<Rating> findByRaterId(Long userId);
    boolean existsByBookingIdAndRaterId(Long bookingId, Long raterId);
} 