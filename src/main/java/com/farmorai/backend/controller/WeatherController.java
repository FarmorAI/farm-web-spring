package com.farmorai.backend.controller;

import com.farmorai.backend.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService){
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Map<String, Object>>> getFilteredWeather(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) String baseDate,
            @RequestParam String baseTime) {

        // ✅ baseDate가 없으면 자동으로 오늘 날짜 설정
        if (baseDate == null || baseDate.isEmpty()) {
            baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        Map<String, Map<String, Object>> filteredWeather = weatherService.getFilteredWeather(lat, lon, baseDate, baseTime);
        return ResponseEntity.ok(filteredWeather);
    }


}
