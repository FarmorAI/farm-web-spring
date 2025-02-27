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

        String uriString = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" +
                "?serviceKey=" + encodedApiKey +
                "&numOfRows=3000" +
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

    public Map<String, Map<String, Object>> getFilteredWeather(double lat, double lon, String baseDate, String baseTime){
        String jsonResponse = getWeather(lat, lon, baseDate, baseTime);
        return extractWeatherData(jsonResponse);
    }

    // 필요 정보만 추출
    public Map<String, Map<String, Object>> extractWeatherData(String jsonResponse){
        Map<String, String> categoryMapping = new HashMap<>();
        categoryMapping.put("TMN", "일 최저기온");
        categoryMapping.put("TMX", "일 최고기온");
        categoryMapping.put("WSD", "풍속");
        categoryMapping.put("SKY", "하늘상태");
        categoryMapping.put("REH", "습도");
        categoryMapping.put("POP", "강수 확률");
        Set<String> targetCategories = categoryMapping.keySet();

        // 날짜별 결과 저장할 Map
        Map<String, Map<String, Object>> dailyWeather = new LinkedHashMap<>();

        String[] lines = jsonResponse.split("\\{");

        for (String line : lines) {
            if (line.contains("\"category\"")) {  // 기상 요소 값 포함된 줄만 필터링
                String category = extractValue(line, "\"category\":\"", "\"");
                String fcstDate = extractValue(line, "\"fcstDate\":\"", "\"");
                String fcstValue = extractValue(line, "\"fcstValue\":\"", "\"");

                if (targetCategories.contains(category) && fcstDate != null && fcstValue != null) {
                    String translatedCategory = categoryMapping.get(category); // 코드명을 한글로 변환

                    dailyWeather.putIfAbsent(fcstDate, new LinkedHashMap<>());

                    if (category.equals("TMN") || category.equals("TMX")) {
                        dailyWeather.get(fcstDate).put(translatedCategory, fcstValue);
                    } else {
                        Map<String, Object> details = (Map<String, Object>) dailyWeather.get(fcstDate)
                                .computeIfAbsent("기타 정보", k -> new LinkedHashMap<>());
                        details.put(translatedCategory, fcstValue);
                    }
                }
            }
        }
        return dailyWeather;
    }

    private String extractValue(String line, String startTag, String endTag) {
        try {
            int startIndex = line.indexOf(startTag);
            if (startIndex == -1) return null;
            startIndex += startTag.length();
            int endIndex = line.indexOf(endTag, startIndex);
            return (endIndex == -1) ? null : line.substring(startIndex, endIndex);
        } catch (Exception e) {
            return null;
        }
    }
}

