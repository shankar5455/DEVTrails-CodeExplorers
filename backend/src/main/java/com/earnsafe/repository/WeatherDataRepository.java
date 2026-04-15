package com.earnsafe.repository;

import com.earnsafe.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findFirstByLatitudeAndLongitudeOrderByFetchedAtDesc(Double latitude, Double longitude);
}
