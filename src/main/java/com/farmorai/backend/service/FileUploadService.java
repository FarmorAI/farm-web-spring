package com.farmorai.backend.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class FileUploadService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String fastApiUrl = "http://localhost:8000/detect";  // FastAPI 서버 주소

    public byte[] sendImage(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Multipart 요청 생성
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getOriginalFilename() + "\"\r\n").getBytes(StandardCharsets.UTF_8));
        outputStream.write(("Content-Type: " + file.getContentType() + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        outputStream.write(file.getBytes());
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(outputStream.toByteArray(), headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(fastApiUrl, HttpMethod.POST, requestEntity, byte[].class);
        return response.getBody();
    }
}