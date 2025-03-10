package com.farmorai.backend.controller;


import com.farmorai.backend.service.FileUploadService;
import com.farmorai.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadUtil fileUploadUtil;
    private final FileUploadService fileUploadService;

    @Value("${com.farmorai.upload.path}")
    private String uploadPath;  // 파일 업로드 경로

    /**
     * Upload and process images
     * @Param files List of files to upload
     * @return processed image와 quality metrics
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("파일 없음"));
            }

            // 첫번째 파일만 처리 (필요에 따라 모든 파일 처리로 확장 가능)
            MultipartFile file = files.get(0);
            Map<String, Object> processingResult = fileUploadService.sendImageToFastApi(file);

            if (processingResult == null || !processingResult.containsKey("image_url")) {
                log.error("FastAPI 응답에 image_url이 없음");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("API 요청 오류"));
            }
            return ResponseEntity.ok(processingResult);

        } catch (Exception e) {
            log.error("파일 업로드 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("처리 중 오류 발생" + e.getMessage()));
        }
    }

    /**
     * Create error response map
     * @param message Error message
     * @return Map with error details
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("success", false);
        return response;
    }


    /**
    * File List 조회 API
    */
    @GetMapping("/files")
    public ResponseEntity<List<String>> getUploadedFiles() {
        File folder = new File(uploadPath);
        String[] fileNames = folder.list();

        if (fileNames == null || fileNames.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(List.of());
        }
        return ResponseEntity.ok(List.of(fileNames));
    }

    /**
     * File Download API
     */
    @GetMapping("files/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        return null;
    }


    /**
     * File Delete API
     */
    @DeleteMapping("/files")
    public ResponseEntity<Map<String, Object>> deleteFiles(@RequestBody List<String> fileNames) {
        return null;
    }
}