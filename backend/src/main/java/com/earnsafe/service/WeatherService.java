package com.earnsafe.service;

import com.earnsafe.dto.WeatherResponse;
import com.earnsafe.entity.WeatherData;
import com.earnsafe.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final RestTemplate restTemplate;
    private final WeatherDataRepository weatherDataRepository;

    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.api.url}")
    private String apiUrl;

    @SuppressWarnings("unchecked")
    public WeatherResponse fetchWeather(Double lat, Double lon) {
        try {
            String url = String.format("%s/weather?lat=%s&lon=%s&appid=%s&units=metric", apiUrl, lat, lon, apiKey);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                log.error("Null response from OpenWeather API");
                return getDefaultWeather();
            }

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            Map<String, Object> wind = (Map<String, Object>) response.get("wind");
            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.get("weather");
            Map<String, Object> rain = (Map<String, Object>) response.get("rain");

            Double temperature = main != null ? toDouble(main.get("temp")) : 25.0;
            Double feelsLike = main != null ? toDouble(main.get("feels_like")) : 25.0;
            Double humidity = main != null ? toDouble(main.get("humidity")) : 50.0;
            Double pressure = main != null ? toDouble(main.get("pressure")) : 1013.0;
            Double windSpeed = wind != null ? toDouble(wind.get("speed")) : 5.0;
            Double rainfall = rain != null ? toDouble(rain.get("1h")) : 0.0;
            Double visibility = response.get("visibility") != null ? toDouble(response.get("visibility")) : 10000.0;
            String weatherMain = weatherList != null && !weatherList.isEmpty() ? (String) weatherList.get(0).get("main") : "Clear";
            String weatherDesc = weatherList != null && !weatherList.isEmpty() ? (String) weatherList.get(0).get("description") : "clear sky";
            String city = (String) response.get("name");

            WeatherData weatherData = WeatherData.builder()
                    .latitude(lat).longitude(lon).city(city)
                    .temperature(temperature).feelsLike(feelsLike)
                    .humidity(humidity).pressure(pressure)
                    .windSpeed(windSpeed).rainfall(rainfall)
                    .visibility(visibility)
                    .weatherMain(weatherMain).weatherDescription(weatherDesc)
                    .build();
            weatherDataRepository.save(weatherData);

            log.info("Weather fetched for ({}, {}): {}°C, {}", lat, lon, temperature, weatherMain);

            return WeatherResponse.builder()
                    .temperature(temperature).feelsLike(feelsLike)
                    .humidity(humidity).pressure(pressure)
                    .windSpeed(windSpeed).rainfall(rainfall)
                    .visibility(visibility)
                    .weatherMain(weatherMain).weatherDescription(weatherDesc)
                    .city(city)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage());
            return getDefaultWeather();
        }
    }

    private WeatherResponse getDefaultWeather() {
        return WeatherResponse.builder()
                .temperature(30.0).feelsLike(32.0).humidity(60.0)
                .pressure(1013.0).windSpeed(5.0).rainfall(0.0)
                .visibility(10000.0).weatherMain("Clear")
                .weatherDescription("clear sky").city("Unknown")
                .build();
    }

    private Double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        return Double.parseDouble(value.toString());
    }
}
