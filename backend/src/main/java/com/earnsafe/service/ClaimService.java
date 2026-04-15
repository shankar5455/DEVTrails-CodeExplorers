package com.earnsafe.service;

import com.earnsafe.dto.*;
import com.earnsafe.entity.*;
import com.earnsafe.enums.*;
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
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final AIService aiService;
    private final PayoutService payoutService;

    public Claim createAutomaticClaim(Policy policy, User user, WeatherResponse weather, DisruptionType disruptionType) {
        String details = buildDisruptionDetails(disruptionType, weather);

        Double claimAmount = calculateClaimAmount(policy.getCoverageAmount(), disruptionType);

        Claim claim = Claim.builder()
                .policy(policy)
                .user(user)
                .disruptionType(disruptionType)
                .disruptionDetails(details)
                .status(ClaimStatus.PENDING)
                .claimAmount(claimAmount)
                .weatherTemp(weather.getTemperature())
                .weatherHumidity(weather.getHumidity())
                .weatherRainfall(weather.getRainfall())
                .weatherWindSpeed(weather.getWindSpeed())
                .build();

        claim = claimRepository.save(claim);
        log.info("Automatic claim created: type={}, amount={}, user={}", disruptionType, claimAmount, user.getEmail());

        processClaim(claim, user, weather);

        return claim;
    }

    private void processClaim(Claim claim, User user, WeatherResponse weather) {
        Long recentClaims = claimRepository.countRecentClaimsByUser(user, LocalDateTime.now().minusDays(30));

        FraudCheckRequest fraudRequest = FraudCheckRequest.builder()
                .userId(user.getId())
                .claimAmount(claim.getClaimAmount())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .userLatitude(user.getLatitude())
                .userLongitude(user.getLongitude())
                .temperature(weather.getTemperature())
                .humidity(weather.getHumidity())
                .rainfall(weather.getRainfall())
                .windSpeed(weather.getWindSpeed())
                .claimFrequency(recentClaims.intValue())
                .totalClaims(claimRepository.countByUser(user).intValue())
                .build();

        FraudCheckResponse fraudResponse = aiService.checkFraud(fraudRequest);

        claim.setFraudScore(fraudResponse.getFraudScore());
        claim.setIsFraudulent(fraudResponse.getIsFraudulent());
        claim.setFraudReason(fraudResponse.getReason());
        claim.setProcessedAt(LocalDateTime.now());

        if (fraudResponse.getIsFraudulent()) {
            claim.setStatus(ClaimStatus.REJECTED);
            log.warn("Claim {} rejected due to fraud: {}", claim.getId(), fraudResponse.getReason());
        } else {
            claim.setStatus(ClaimStatus.APPROVED);
            log.info("Claim {} approved, initiating payout", claim.getId());
            payoutService.processPayout(claim, user);
        }

        claimRepository.save(claim);
    }

    public List<ClaimResponse> getUserClaims(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.earnsafe.exception.ResourceNotFoundException("User not found"));
        return claimRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    private String buildDisruptionDetails(DisruptionType type, WeatherResponse weather) {
        return switch (type) {
            case HEAVY_RAIN -> String.format("Heavy rainfall detected: %.1fmm/h, humidity: %.0f%%",
                    weather.getRainfall(), weather.getHumidity());
            case EXTREME_HEAT -> String.format("Extreme heat detected: %.1f°C, feels like: %.1f°C",
                    weather.getTemperature(), weather.getFeelsLike());
            case STORM -> String.format("Storm detected: wind speed %.1fm/s, rainfall: %.1fmm/h",
                    weather.getWindSpeed(), weather.getRainfall());
            case HIGH_POLLUTION -> String.format("High pollution detected: AQI above threshold");
            case FLOOD -> String.format("Flood risk: rainfall %.1fmm/h, humidity %.0f%%",
                    weather.getRainfall(), weather.getHumidity());
        };
    }

    private Double calculateClaimAmount(Double coverageAmount, DisruptionType type) {
        double percentage = switch (type) {
            case HEAVY_RAIN -> 0.3;
            case EXTREME_HEAT -> 0.2;
            case STORM -> 0.5;
            case HIGH_POLLUTION -> 0.15;
            case FLOOD -> 0.6;
        };
        return Math.round(coverageAmount * percentage * 100.0) / 100.0;
    }

    public ClaimResponse mapToResponse(Claim claim) {
        return ClaimResponse.builder()
                .id(claim.getId())
                .policyId(claim.getPolicy().getId())
                .userId(claim.getUser().getId())
                .userName(claim.getUser().getFullName())
                .disruptionType(claim.getDisruptionType())
                .disruptionDetails(claim.getDisruptionDetails())
                .status(claim.getStatus())
                .claimAmount(claim.getClaimAmount())
                .fraudScore(claim.getFraudScore())
                .isFraudulent(claim.getIsFraudulent())
                .fraudReason(claim.getFraudReason())
                .triggeredAt(claim.getTriggeredAt())
                .processedAt(claim.getProcessedAt())
                .build();
    }
}
