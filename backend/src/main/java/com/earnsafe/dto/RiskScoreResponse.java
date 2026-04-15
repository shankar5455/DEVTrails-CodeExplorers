package com.earnsafe.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RiskScoreResponse {
    private Double riskScore;
    private Double suggestedPremium;
    private String riskLevel;
}
