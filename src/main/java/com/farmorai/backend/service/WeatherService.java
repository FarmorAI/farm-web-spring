package com.farmorai.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;

@Service
public class WeatherService {
    private final String weatherApiKey = "10hq%2FXQHlvOAFMbPmF7Iwe0j1bOYBeh2x0dh6Budm8HVNXqsQpPcwYF3Z5r%2F0r%2FYoFAMpK%2BYg1ztyXBLMNE9xw%3D%3D";
    private final RestTemplate restTemplate = new RestTemplate();

    // 사용자의 위치 가져오기
    public String getLocation(){
        String apiUrl = "http://ip-api.com/json";
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        String city = (String)response.get("city");
        String region = (String)response.get("regionName");

        return region != null ? region : city;    }

    public String getCurrentDate(){
        return LocalDate.now().toString();
    }

    public String getWeather(){
        String location = getLocation();
        String currentDate = getCurrentDate();

        String apiUrl = "http://apis.data.go.kr/1390802/AgriWeather/WeatherObsrInfo/V2/GnrlWeather" +
                "?serviceKey=" + weatherApiKey +
                "&Page_No=1" +
                "&Page_Size=20" +
                "&date_Time=" + currentDate + // 현재 날짜 자동 입력
                "&obsr_Spot_Nm=" + location +
                "&dataType=json";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl,String.class);
        return response.getBody();
    }
}
