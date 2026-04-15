package com.earnsafe.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FraudCheckResponse {
    private Boolean isFraudulent;
    private Double fraudScore;
    private String reason;
}
