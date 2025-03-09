package com.farmorai.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class FileUploadService {
    private final RestTemplate restTemplate;

    @Value("${fastapi.server.url}")
    private String fastApiUrl;

    public FileUploadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 이미지 파일을 FastAPI 서버로 전송하고 응답을 받습니다.
     * @param file 전송할 이미지 파일
     * @return FastAPI 서버로부터 받은 응답 데이터
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public byte[] sendImageToFastApi(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", createFileResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Fast API 요청
        ResponseEntity<byte[]> response = restTemplate.exchange(
                fastApiUrl,
                HttpMethod.POST,
                requestEntity,
                byte[].class   // 응답 데이터를 byte[]로 받음.
        );
        return response.getBody();
    }

    /**
     * MultipartFile -> ByteArrayResource 변환
     */
    private ByteArrayResource createFileResource(final MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }

            @Override
            public long contentLength() {
                return file.getSize();
            }
        };
    }
}