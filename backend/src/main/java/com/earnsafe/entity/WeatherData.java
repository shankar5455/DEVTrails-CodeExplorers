package com.earnsafe.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WeatherData {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;
    private String city;

    private Double temperature;
    private Double feelsLike;
    private Double humidity;
    private Double pressure;
    private Double windSpeed;
    private Double rainfall;
    private Double visibility;
    private Integer aqi;

    private String weatherMain;
    private String weatherDescription;

    private LocalDateTime fetchedAt;

    @PrePersist
    protected void onCreate() { this.fetchedAt = LocalDateTime.now(); }
}
