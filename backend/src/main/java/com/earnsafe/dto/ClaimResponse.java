package com.earnsafe.dto;

import com.earnsafe.enums.ClaimStatus;
import com.earnsafe.enums.DisruptionType;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClaimResponse {
    private Long id;
    private Long policyId;
    private Long userId;
    private String userName;
    private DisruptionType disruptionType;
    private String disruptionDetails;
    private ClaimStatus status;
    private Double claimAmount;
    private Double fraudScore;
    private Boolean isFraudulent;
    private String fraudReason;
    private LocalDateTime triggeredAt;
    private LocalDateTime processedAt;
}
