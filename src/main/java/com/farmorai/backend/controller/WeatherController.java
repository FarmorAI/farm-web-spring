package com.farmorai.backend.controller;

import com.farmorai.backend.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService){
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<Map<String,Object>> getWeather(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam String baseDate,
            @RequestParam String baseTime){
        Map<String, Object> filteredWeather = weatherService.getFilteredWeather(lat, lon, baseDate, baseTime);
        return ResponseEntity.ok(filteredWeather);
    }
}
