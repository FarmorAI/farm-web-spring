package com.farmorai.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/distribution")
public class AnalysisController {

    private static final String FASTAPI_URL = "http://localhost:9090/fit_distribution";

    @GetMapping("/get")
    public ResponseEntity<List<Map<String, Object>>> getDistributionData() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(FASTAPI_URL, String.class);

        // ✅ Jackson의 ObjectMapper 사용
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode distributions = rootNode.get("distribution");

        List<Map<String, Object>> result = new ArrayList<>();

        for (JsonNode gradeData : distributions) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("grade", gradeData.get("grade").asInt());
            dataMap.put("mu", gradeData.get("mu").asDouble());
            dataMap.put("std", gradeData.get("std").asDouble());
            dataMap.put("mean", gradeData.get("mean").asDouble());
            dataMap.put("x", objectMapper.convertValue(gradeData.get("x"), List.class));
            dataMap.put("y", objectMapper.convertValue(gradeData.get("y"), List.class));

            // ✅ FastAPI에서 받아온 `threshold_75` 추가
            if (gradeData.has("threshold_75")) {
                dataMap.put("threshold_75", gradeData.get("threshold_75").asDouble());
            } else {
                System.out.println("⚠️ threshold_75 값이 FastAPI 응답에 없음!"); // 디버깅 로그
            }

            result.add(dataMap);
        }

        return ResponseEntity.ok(result);
    }
}
