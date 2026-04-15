package com.earnsafe.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDashboard {
    private String fullName;
    private String email;
    private PolicyResponse activePolicy;
    private Double weeklyPremium;
    private Double earningsProtected;
    private Long totalClaims;
    private Long approvedClaims;
    private List<ClaimResponse> claimsHistory;
    private Double currentRiskScore;
}
