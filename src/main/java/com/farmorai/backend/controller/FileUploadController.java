package com.farmorai.backend.controller;


import com.farmorai.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpMethod.*;

@RestController
@RequestMapping("/api")
@Log4j2
@RequiredArgsConstructor
public class FileUploadController {


    private final FileUploadUtil fileUploadUtil;
    private static final String FASTAPI_SERVER_URL = "http://localhost:8000/detect";

    @Value("${com.farmorai.upload.path}")
    private String uploadPath;


    /**
     * React 에서 파일 업로드 → Spring Boot 가 저장 후 FastAPI 로 전송
     */
    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        // Spring Boot에서 파일 저장
        List<String> savedFiles = fileUploadUtil.saveFiles(files);

        log.info("파일 저장 성공: {}", savedFiles);
        try {
            for (String fileName : savedFiles) {
                File file = new File(uploadPath+"/"+fileName);
                Path filePath = Paths.get(file.getAbsolutePath());
                byte[] fileBytes = Files.readAllBytes(filePath);

                // FastAPI에 파일을 멀티파트 폼데이터 형식으로 전송
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", new ByteArrayResource(fileBytes) {
                    @Override
                    public String getFilename() {
                        return fileName; // FastAPI 에서 원본 파일 이름 유지
                    }
                });

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                ResponseEntity<byte[]> response = restTemplate.exchange(
                        FASTAPI_SERVER_URL,
                        POST,
                        requestEntity,
                        byte[].class
                );

                log.info("FastAPI 요청 결과: {}", response);

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("✅ FastAPI로 파일 전송 성공!");
                    // FastAPI에서 받은 객체 탐지된 이미지를 저장
                    String detectedFilePath = uploadPath + "/detected_" + fileName;
                    Files.write(Paths.get(detectedFilePath), Objects.requireNonNull(response.getBody()));

                    log.info("📁 객체 탐지된 이미지 저장 완료: " + detectedFilePath);
                    //  FastAPI에서 받은 객체 탐지된 이미지를 즉시 React에 응답
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.setContentType(MediaType.IMAGE_JPEG);
                    return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
                } else {
                    log.error("❌ FastAPI 전송 실패: " + response.getBody());
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            log.error("FastAPI 요청 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * 업로드된 파일 다운로드 API
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        return fileUploadUtil.getFile(fileName);
    }

    /**
     * 파일 삭제 API
     */
    @DeleteMapping
    public ResponseEntity<String> deleteFiles(@RequestBody List<String> fileNames) {
        try {
            fileUploadUtil.deleteFile(fileNames);
            return ResponseEntity.ok("파일 삭제 성공");
        } catch (RuntimeException e) {
            log.error("파일 삭제 실패", e);
            return ResponseEntity.internalServerError().body("파일 삭제 실패");
        }
    }


}