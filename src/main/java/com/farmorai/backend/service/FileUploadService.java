package com.farmorai.backend.service;

import com.farmorai.backend.dto.AiResultDto;
import com.farmorai.backend.mapper.AiResultMapper;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FileUploadService {
    private final AiResultMapper aiResultMapper;
    private final WebClient webClient;

    public FileUploadService(
            @Value("${fastapi.server.url}") String fastApiUrl,
            WebClient.Builder webClientBuilder,
            AiResultMapper aiResultMapper
    ) {
        this.webClient = webClientBuilder.baseUrl(fastApiUrl).build();
        this.aiResultMapper = aiResultMapper;
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


    // AI 분석 결과 전체 조회
    public List<AiResultDto> getAiResultList(Long memberId) {
        return aiResultMapper.getAiResultList(memberId);
    }

    // AI 분석 결과 상세 조회
    public AiResultDto getAiResult(Long aiResultId) {
        return aiResultMapper.getAiResult(aiResultId);
    }

    // AI 분석 결과 삭제
    public void deleteAiResult(Long aiResultId) {
        aiResultMapper.deleteAiResult(aiResultId);
    }

    // AI 분석 결과 저장
    public AiResultDto insertAiResult(Map<String, Object> result, CustomUserDetails userDetails) {
        Map<String, Double> quality = (Map<String, Double>) result.get("quality");

        AiResultDto aiResultDto = AiResultDto.builder()
                .aiResultId(null)
                .rateS(quality.get("특"))
                .rateA(quality.get("상"))
                .rateB(quality.get("보통"))
                .imageUrl((String) result.get("image_url"))
                .memberId(userDetails.getMemberId())
                .createdAt(null)
                .build();

        aiResultMapper.insertAiResult(aiResultDto);
        return aiResultDto;
    }
}