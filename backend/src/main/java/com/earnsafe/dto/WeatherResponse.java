package com.earnsafe.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WeatherResponse {
    private Double temperature;
    private Double feelsLike;
    private Double humidity;
    private Double pressure;
    private Double windSpeed;
    private Double rainfall;
    private Double visibility;
    private String weatherMain;
    private String weatherDescription;
    private String city;
}
