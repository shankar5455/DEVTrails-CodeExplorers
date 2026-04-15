package com.earnsafe.scheduler;

import com.earnsafe.service.ParametricTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherScheduler {

    private final ParametricTriggerService parametricTriggerService;

    @Scheduled(fixedRate = 1800000)
    public void checkWeatherAndTriggerClaims() {
        log.info("=== Scheduled weather check started ===");
        try {
            parametricTriggerService.checkAndTriggerClaims();
        } catch (Exception e) {
            log.error("Error in scheduled weather check: {}", e.getMessage());
        }
        log.info("=== Scheduled weather check completed ===");
    }
}
