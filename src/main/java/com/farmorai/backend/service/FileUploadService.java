package com.farmorai.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class FileUploadService {
    private final WebClient webClient;

    public FileUploadService(
            WebClient.Builder webClientBuilder,
            @Value("${fastapi.server.url}") String fastApiUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(fastApiUrl).build();
    }

    /**
     * 이미지 파일을 FastAPI 서버로 전송하고 응답을 받습니다.
     * @param file 전송할 이미지 파일
     * @return FastAPI 서버로부터 받은 응답 데이터를 Mono 형태로 반환
     */
    public Mono<Map<String, Object>> sendImageToFastApi(MultipartFile file) {
        // Multipart 요청 생성
        Mono<Map<String, Object>> responseMono = webClient.post()
                .uri("/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});

        return responseMono;
    }
}