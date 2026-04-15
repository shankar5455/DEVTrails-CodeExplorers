package com.earnsafe.service;

import com.earnsafe.dto.WeatherResponse;
import com.earnsafe.entity.*;
import com.earnsafe.enums.*;
import com.earnsafe.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParametricTriggerService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final WeatherService weatherService;
    private final ClaimService claimService;

    private static final double HEAVY_RAIN_THRESHOLD = 15.0;
    private static final double EXTREME_HEAT_THRESHOLD = 42.0;
    private static final double STORM_WIND_THRESHOLD = 20.0;
    private static final double FLOOD_RAIN_THRESHOLD = 30.0;

    public void checkAndTriggerClaims() {
        List<Policy> activePolicies = policyRepository.findByStatus(PolicyStatus.ACTIVE);
        log.info("Checking {} active policies for parametric triggers", activePolicies.size());

        for (Policy policy : activePolicies) {
            if (policy.getEndDate() != null && policy.getEndDate().isBefore(LocalDateTime.now())) {
                policy.setStatus(PolicyStatus.EXPIRED);
                policyRepository.save(policy);
                log.info("Policy {} expired", policy.getId());
                continue;
            }

            User user = policy.getUser();
            WeatherResponse weather = weatherService.fetchWeather(user.getLatitude(), user.getLongitude());

            checkHeavyRain(policy, user, weather);
            checkExtremeHeat(policy, user, weather);
            checkStorm(policy, user, weather);
            checkFlood(policy, user, weather);
        }
    }

    private void checkHeavyRain(Policy policy, User user, WeatherResponse weather) {
        if (weather.getRainfall() != null && weather.getRainfall() > HEAVY_RAIN_THRESHOLD) {
            if (!hasRecentClaim(user, DisruptionType.HEAVY_RAIN)) {
                log.info("Heavy rain detected for user {}: {}mm/h", user.getEmail(), weather.getRainfall());
                claimService.createAutomaticClaim(policy, user, weather, DisruptionType.HEAVY_RAIN);
            }
        }
    }

    private void checkExtremeHeat(Policy policy, User user, WeatherResponse weather) {
        if (weather.getTemperature() != null && weather.getTemperature() > EXTREME_HEAT_THRESHOLD) {
            if (!hasRecentClaim(user, DisruptionType.EXTREME_HEAT)) {
                log.info("Extreme heat detected for user {}: {}°C", user.getEmail(), weather.getTemperature());
                claimService.createAutomaticClaim(policy, user, weather, DisruptionType.EXTREME_HEAT);
            }
        }
    }

    private void checkStorm(Policy policy, User user, WeatherResponse weather) {
        if (weather.getWindSpeed() != null && weather.getWindSpeed() > STORM_WIND_THRESHOLD) {
            if (!hasRecentClaim(user, DisruptionType.STORM)) {
                log.info("Storm detected for user {}: {}m/s wind", user.getEmail(), weather.getWindSpeed());
                claimService.createAutomaticClaim(policy, user, weather, DisruptionType.STORM);
            }
        }
    }

    private void checkFlood(Policy policy, User user, WeatherResponse weather) {
        if (weather.getRainfall() != null && weather.getRainfall() > FLOOD_RAIN_THRESHOLD) {
            if (!hasRecentClaim(user, DisruptionType.FLOOD)) {
                log.info("Flood risk detected for user {}: {}mm/h rainfall", user.getEmail(), weather.getRainfall());
                claimService.createAutomaticClaim(policy, user, weather, DisruptionType.FLOOD);
            }
        }
    }

    private boolean hasRecentClaim(User user, DisruptionType type) {
        Long recentClaims = claimRepository.countRecentClaimsByUserAndType(user, type, LocalDateTime.now().minusHours(6));
        return recentClaims > 0;
    }
}
