package com.earnsafe.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStats {
    private Long totalUsers;
    private Long totalPolicies;
    private Long activePolicies;
    private Long totalClaims;
    private Long approvedClaims;
    private Long rejectedClaims;
    private Long fraudulentClaims;
    private Double totalPremiumCollected;
    private Double totalPayouts;
    private List<ClaimResponse> recentClaims;
}
