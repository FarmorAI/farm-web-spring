package com.farmorai.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfoService {

    @Value("${KAKAO_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<Map<String, Object>> getInfo(String query) {
        try {
            String url = "https://dapi.kakao.com/v2/search/blog?query=" + query + "&size=300&page=1&sort=accuracy";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + apiKey);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            List<Map<String, Object>> filteredResults = ((List<Map<String, Object>>) response.getBody().get("documents"))
                    .stream()
                    .map(this::removeHtmlTags)  // HTML 태그 제거
                    .filter(post -> post.get("thumbnail") != null && !((String) post.get("thumbnail")).isEmpty()) // 썸네일 없는 것 제외
                    .collect(Collectors.toList());

            return new ResponseEntity<>(Map.of("documents", filteredResults), response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private Map<String, Object> removeHtmlTags(Map<String, Object> post) {
        Pattern htmlPattern = Pattern.compile("<[^>]*>");
        Map<String, Object> cleanPost = new HashMap<>(post);

        cleanPost.put("title", htmlPattern.matcher((String) post.get("title")).replaceAll(""));
        cleanPost.put("contents", htmlPattern.matcher((String) post.get("contents")).replaceAll(""));

        return cleanPost;
    }

}
