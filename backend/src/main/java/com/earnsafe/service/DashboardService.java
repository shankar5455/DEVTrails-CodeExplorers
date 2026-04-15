package com.earnsafe.service;

import com.earnsafe.dto.*;
import com.earnsafe.entity.*;
import com.earnsafe.enums.*;
import com.earnsafe.exception.ResourceNotFoundException;
import com.earnsafe.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final ClaimService claimService;

    public UserDashboard getUserDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PolicyResponse activePolicy = policyRepository
                .findFirstByUserAndStatusOrderByCreatedAtDesc(user, PolicyStatus.ACTIVE)
                .map(p -> PolicyResponse.builder()
                        .id(p.getId())
                        .premiumAmount(p.getPremiumAmount())
                        .coverageAmount(p.getCoverageAmount())
                        .riskScore(p.getRiskScore())
                        .status(p.getStatus())
                        .startDate(p.getStartDate())
                        .endDate(p.getEndDate())
                        .build())
                .orElse(null);

        List<ClaimResponse> claims = claimRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(claimService::mapToResponse)
                .collect(Collectors.toList());

        Long totalClaims = claimRepository.countByUser(user);
        Long approvedClaims = claims.stream().filter(c -> c.getStatus() == ClaimStatus.APPROVED || c.getStatus() == ClaimStatus.PAID).count();

        Double earningsProtected = claims.stream()
                .filter(c -> c.getStatus() == ClaimStatus.PAID)
                .mapToDouble(ClaimResponse::getClaimAmount)
                .sum();

        return UserDashboard.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .activePolicy(activePolicy)
                .weeklyPremium(activePolicy != null ? activePolicy.getPremiumAmount() : 0.0)
                .earningsProtected(earningsProtected)
                .totalClaims(totalClaims)
                .approvedClaims(approvedClaims)
                .claimsHistory(claims)
                .currentRiskScore(activePolicy != null ? activePolicy.getRiskScore() : 0.0)
                .build();
    }

    public DashboardStats getAdminDashboard() {
        List<ClaimResponse> recentClaims = claimRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(claimService::mapToResponse)
                .collect(Collectors.toList());

        return DashboardStats.builder()
                .totalUsers(userRepository.count())
                .totalPolicies(policyRepository.count())
                .activePolicies((long) policyRepository.findByStatus(PolicyStatus.ACTIVE).size())
                .totalClaims(claimRepository.count())
                .approvedClaims(claimRepository.countByStatus(ClaimStatus.APPROVED) + claimRepository.countByStatus(ClaimStatus.PAID))
                .rejectedClaims(claimRepository.countByStatus(ClaimStatus.REJECTED))
                .fraudulentClaims(claimRepository.countByIsFraudulentTrue())
                .totalPremiumCollected(policyRepository.getTotalPremiumCollected())
                .totalPayouts(claimRepository.getTotalPayouts())
                .recentClaims(recentClaims)
                .build();
    }
}
