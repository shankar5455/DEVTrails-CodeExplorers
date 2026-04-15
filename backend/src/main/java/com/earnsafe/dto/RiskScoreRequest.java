package com.earnsafe.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RiskScoreRequest {
    private Double temperature;
    private Double humidity;
    private Double rainfall;
    private Double windSpeed;
    private Double latitude;
    private Double longitude;
    private Integer historicalClaims;
    private Double aqi;
}
