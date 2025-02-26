package com.farmorai.backend.service;


import com.farmorai.backend.util.GridConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class WeatherService {

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

        URI uri = UriComponentsBuilder.fromHttpUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst")
                .queryParam("serviceKey", apiKey) // 📌 자동 URL 인코딩됨
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", grid.nx)  // 격자 변환된 값 사용
                .queryParam("ny", grid.ny)
                .encode()  // 📌 자동 URL 인코딩
                .build()
                .toUri();
/*        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst" +
                "?serviceKey=" + apiKey +
                "&numOfRows=10" +
                "&pageNo=1" +
                "&dataType=JSON" +  // JSON 형식으로 응답 받기
                "&base_date=" + baseDate +
                "&base_time=" + baseTime +
                "&nx=" + grid.nx +
                "&ny=" + grid.ny;*/

        return restTemplate.getForObject(uri, String.class);
    }

}
