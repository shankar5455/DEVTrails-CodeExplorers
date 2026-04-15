package com.earnsafe.entity;

import com.earnsafe.enums.ClaimStatus;
import com.earnsafe.enums.DisruptionType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Claim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private DisruptionType disruptionType;

    private String disruptionDetails;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private Double claimAmount;
    private Double fraudScore;
    private Boolean isFraudulent;
    private String fraudReason;

    private Double weatherTemp;
    private Double weatherHumidity;
    private Double weatherRainfall;
    private Double weatherWindSpeed;

    private LocalDateTime triggeredAt;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.triggeredAt = LocalDateTime.now();
    }
}
