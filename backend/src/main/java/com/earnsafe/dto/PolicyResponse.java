package com.earnsafe.dto;

import com.earnsafe.enums.PolicyStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PolicyResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Double premiumAmount;
    private Double coverageAmount;
    private Double riskScore;
    private PolicyStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
}
