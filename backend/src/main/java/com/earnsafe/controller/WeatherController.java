package com.earnsafe.controller;

import com.earnsafe.dto.WeatherResponse;
import com.earnsafe.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam Double lat, @RequestParam Double lon) {
        return ResponseEntity.ok(weatherService.fetchWeather(lat, lon));
    }
}
