package com.earnsafe.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FraudCheckRequest {
    private Long userId;
    private Double claimAmount;
    private Double latitude;
    private Double longitude;
    private Double userLatitude;
    private Double userLongitude;
    private Double temperature;
    private Double humidity;
    private Double rainfall;
    private Double windSpeed;
    private Integer claimFrequency;
    private Integer totalClaims;
}
