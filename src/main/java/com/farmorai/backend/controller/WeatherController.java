package com.farmorai.backend.controller;

import com.farmorai.backend.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService){
        this.weatherService = weatherService;
    }

    @GetMapping
    public String getWeather(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam String baseDate,
            @RequestParam String baseTime
    ){
        return weatherService.getWeather(lat, lon, baseDate, baseTime);
    }
}
