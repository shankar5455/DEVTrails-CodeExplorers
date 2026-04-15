package com.earnsafe.service;

import com.earnsafe.dto.*;
import com.earnsafe.entity.*;
import com.earnsafe.enums.PolicyStatus;
import com.earnsafe.exception.*;
import com.earnsafe.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final AIService aiService;
    private final WeatherService weatherService;
    private final ClaimRepository claimRepository;

    public PolicyResponse purchasePolicy(String email, PolicyRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        policyRepository.findFirstByUserAndStatusOrderByCreatedAtDesc(user, PolicyStatus.ACTIVE)
                .ifPresent(p -> { throw new BadRequestException("You already have an active policy"); });

        WeatherResponse weather = weatherService.fetchWeather(user.getLatitude(), user.getLongitude());

        int historicalClaims = claimRepository.countByUser(user).intValue();
        RiskScoreRequest riskRequest = RiskScoreRequest.builder()
                .temperature(weather.getTemperature())
                .humidity(weather.getHumidity())
                .rainfall(weather.getRainfall())
                .windSpeed(weather.getWindSpeed())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .historicalClaims(historicalClaims)
                .build();

        RiskScoreResponse riskResponse = aiService.getRiskScore(riskRequest);

        Double coverageAmount = request.getCoverageAmount() != null ? request.getCoverageAmount() : 5000.0;

        Policy policy = Policy.builder()
                .user(user)
                .premiumAmount(riskResponse.getSuggestedPremium())
                .coverageAmount(coverageAmount)
                .riskScore(riskResponse.getRiskScore())
                .status(PolicyStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        policy = policyRepository.save(policy);
        log.info("Policy purchased for user {}: premium={}, risk={}", email, policy.getPremiumAmount(), policy.getRiskScore());

        return mapToResponse(policy);
    }

    public List<PolicyResponse> getUserPolicies(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return policyRepository.findByUser(user).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public PolicyResponse getActivePolicy(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return policyRepository.findFirstByUserAndStatusOrderByCreatedAtDesc(user, PolicyStatus.ACTIVE)
                .map(this::mapToResponse)
                .orElse(null);
    }

    public List<PolicyResponse> getAllPolicies() {
        return policyRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    private PolicyResponse mapToResponse(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .userId(policy.getUser().getId())
                .userName(policy.getUser().getFullName())
                .premiumAmount(policy.getPremiumAmount())
                .coverageAmount(policy.getCoverageAmount())
                .riskScore(policy.getRiskScore())
                .status(policy.getStatus())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .createdAt(policy.getCreatedAt())
                .build();
    }
}
