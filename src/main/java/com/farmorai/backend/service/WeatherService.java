package com.farmorai.backend.service;


import com.farmorai.backend.util.GridConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class WeatherService {
    private static final Logger logger = LogManager.getLogger(WeatherService.class);

    @Value("${WEATHER_API_KEY}")
    private String apiKey;

    private final GridConvertUtil gridConvertUtil;
    private final RestTemplate restTemplate;

    public WeatherService(GridConvertUtil gridConvertUtil) {
        this.gridConvertUtil = gridConvertUtil;
        this.restTemplate = new RestTemplate();
    }

    public String getWeather(double lat, double lon, String baseDate, String baseTime) {
        // 위도, 경도 기상청 격자로 변환
        GridConvertUtil.Grid grid = gridConvertUtil.convertToGrid(lat, lon);

        String encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

        String uriString = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst" +
                "?serviceKey=" + encodedApiKey +
                "&numOfRows=1000" +
                "&pageNo=1" +
                "&dataType=JSON" +
                "&base_date=" + baseDate +
                "&base_time=" + baseTime +
                "&nx=" + grid.nx +
                "&ny=" + grid.ny;

        final URI uri = URI.create(uriString);

        logger.info("🔑 API Key: {}", apiKey);
        logger.info("🛠 최종 요청 URL: {}", uri);

        return restTemplate.getForObject(uri, String.class);
    }

    public Map<String, Object> getFilteredWeather(double lat, double lon, String baseDate, String baseTime){
        String jsonResponse = getWeather(lat, lon, baseDate, baseTime);
        return extractWeatherData(jsonResponse);
    }

    // 필요 정보만 추출
    public Map<String,Object> extractWeatherData(String jsonResponse){
        Map<String, String> categoryMapping = new HashMap<>();
        categoryMapping.put("T1H", "기온");
        categoryMapping.put("RN1", "강수량");
        categoryMapping.put("SKY", "하늘상태");
        categoryMapping.put("REH", "습도");
        categoryMapping.put("WSD", "풍속");

        Set<String> targetCategories = categoryMapping.keySet();

        Map<String, Object> result = new LinkedHashMap<>();

        String[] lines = jsonResponse.split("\\{");

        for(String line : lines){
            if(line.contains("\"category\"")){
                String category = extractValue(line, "\"category\":\"", "\"");
                String fcstTime = extractValue(line, "\"fcstTime\":\"", "\"");
                String fcstValue = extractValue(line, "\"fcstValue\":\"", "\"");

                // 8시간 동안의 데이터만 저장 (한글 이름 변환 적용)
                if (targetCategories.contains(category) && fcstTime != null && fcstValue != null) {
                    String translatedCategory = categoryMapping.get(category); // 코드명을 한글로 변환
                    result.put(fcstTime + "_" + translatedCategory, fcstValue);
                }
            }
        }
        return result;
    }

    private String extractValue(String line, String startTag, String endTag){
        try{
            int startIndex = line.indexOf(startTag);
            if(startIndex == -1) return null;
            startIndex += startTag.length();
            int endIndex = line.indexOf(endTag, startIndex);
            return (endIndex == -1) ? null : line.substring(startIndex, endIndex);
        } catch(Exception e){
            return null;
        }
    }
}

