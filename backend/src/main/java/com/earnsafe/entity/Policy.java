package com.earnsafe.entity;

import com.earnsafe.enums.PolicyStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "policies")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Policy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double premiumAmount;

    @Column(nullable = false)
    private Double coverageAmount;

    private Double riskScore;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
