package com.earnsafe.service;

import com.earnsafe.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public RiskScoreResponse getRiskScore(RiskScoreRequest request) {
        try {
            String url = aiServiceUrl + "/predict/risk";
            RiskScoreResponse response = restTemplate.postForObject(url, request, RiskScoreResponse.class);
            if (response != null) {
                log.info("Risk score received: {} ({})", response.getRiskScore(), response.getRiskLevel());
                return response;
            }
        } catch (Exception e) {
            log.warn("AI service unavailable for risk scoring, using fallback: {}", e.getMessage());
        }
        return fallbackRiskScore(request);
    }

    public FraudCheckResponse checkFraud(FraudCheckRequest request) {
        try {
            String url = aiServiceUrl + "/predict/fraud";
            FraudCheckResponse response = restTemplate.postForObject(url, request, FraudCheckResponse.class);
            if (response != null) {
                log.info("Fraud check result: fraudulent={}, score={}", response.getIsFraudulent(), response.getFraudScore());
                return response;
            }
        } catch (Exception e) {
            log.warn("AI service unavailable for fraud detection, using fallback: {}", e.getMessage());
        }
        return fallbackFraudCheck(request);
    }

    private RiskScoreResponse fallbackRiskScore(RiskScoreRequest request) {
        double tempScore = Math.min(request.getTemperature() != null ? Math.abs(request.getTemperature() - 25) / 25.0 : 0, 1.0);
        double humidityScore = Math.min(request.getHumidity() != null ? request.getHumidity() / 100.0 : 0, 1.0);
        double rainfallScore = Math.min(request.getRainfall() != null ? request.getRainfall() / 50.0 : 0, 1.0);
        double windScore = Math.min(request.getWindSpeed() != null ? request.getWindSpeed() / 30.0 : 0, 1.0);

        double riskScore = (tempScore * 0.2 + humidityScore * 0.15 + rainfallScore * 0.4 + windScore * 0.25) * 100;
        riskScore = Math.min(Math.max(riskScore, 5), 95);

        double basePremium = 50.0;
        double suggestedPremium = basePremium + (riskScore / 100.0) * 150.0;

        String riskLevel = riskScore < 30 ? "LOW" : riskScore < 60 ? "MEDIUM" : "HIGH";

        return RiskScoreResponse.builder()
                .riskScore(Math.round(riskScore * 100.0) / 100.0)
                .suggestedPremium(Math.round(suggestedPremium * 100.0) / 100.0)
                .riskLevel(riskLevel)
                .build();
    }

    private FraudCheckResponse fallbackFraudCheck(FraudCheckRequest request) {
        double fraudScore = 0.0;
        StringBuilder reason = new StringBuilder();

        if (request.getUserLatitude() != null && request.getUserLongitude() != null
                && request.getLatitude() != null && request.getLongitude() != null) {
            double distance = haversineDistance(request.getUserLatitude(), request.getUserLongitude(),
                    request.getLatitude(), request.getLongitude());
            if (distance > 100) {
                fraudScore += 0.4;
                reason.append("Location mismatch (").append(Math.round(distance)).append("km). ");
            }
        }

        if (request.getClaimFrequency() != null && request.getClaimFrequency() > 3) {
            fraudScore += 0.3;
            reason.append("High claim frequency (").append(request.getClaimFrequency()).append("). ");
        }

        if (request.getRainfall() != null && request.getRainfall() < 5 && request.getClaimAmount() != null && request.getClaimAmount() > 500) {
            fraudScore += 0.3;
            reason.append("Weather inconsistency. ");
        }

        boolean isFraudulent = fraudScore > 0.5;

        return FraudCheckResponse.builder()
                .isFraudulent(isFraudulent)
                .fraudScore(Math.round(fraudScore * 100.0) / 100.0)
                .reason(reason.length() > 0 ? reason.toString().trim() : "No anomalies detected")
                .build();
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
