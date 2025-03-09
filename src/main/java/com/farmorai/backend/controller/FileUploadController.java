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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
     * "React"에서 업로드 된 파일을 "SpringBoot"가 저장 후, "FastAPI"로 전송
     * "?"은 제네릭 와일드 카드로 모든 타입 반환 가능
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            // "SpringBoot"에서 파일 저장
            List<String> savedFiles = fileUploadUtil.saveFiles(files);
            log.info("파일 저장 성공: {}", savedFiles);

            if (savedFiles.isEmpty()) {
                return ResponseEntity.badRequest().body("파일 저장에 실패했습니다.");
            }

            // 첫번째 파일만 처리 (필요에 따라 모든 파일 처리로 확장 가능)
            for (String fileName : savedFiles) {
                byte[] processedImage = processFileWithFastApi(fileName);

                if (processedImage == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("FastAPI 처리 중 오류가 발생했습니다.");
                }
            }

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(processedImage, responseHeaders, HttpStatus.OK);

        } catch (Exception e) {
            log.error("파일 업로드 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 처리 중 오류가 발생하였습니다." + e.getMessage());
        }
    }


    /**
     * "FastAPI"로 파일 전송 후, 처리된 이미지를 저장하고 반환
     */
    private byte[] processFileWithFastApi(String fileName) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(uploadPath, fileName));
            MultipartFile file = fileUploadUtil.createMultipartFile(fileBytes, fileName);
            byte[] processedImage = fileUploadService.sendImageToFastApi(file);

            if (processedImage != null) {
                // "FastAPI"에서 받은 객체 탐지된 이미지 저장
                String detectedFilePath = uploadPath + "/detected_" + fileName;
                Files.write(Paths.get(detectedFilePath), processedImage);
                log.info("객체 탐지된 이미지 저장 완료: {}", detectedFilePath);
                return processedImage;
            }

            log.error("FastAPI 응답이 null 입니다.");
            return null;
        } catch (Exception e) {
            log.error("FastAPI 요청 처리 실패", e);
            return null;
        }
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
        return fileUploadUtil.getFile(fileName);
    }


    /**
     * 파일 삭제 API
     */
    @DeleteMapping("/files")
    public ResponseEntity<String> deleteFiles(@RequestBody List<String> fileNames) {
        try {
            fileUploadUtil.deleteFile(fileNames);
            return ResponseEntity.ok("파일 삭제 성공");
        } catch (RuntimeException e) {
            log.error("파일 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 삭세 실패: " + e.getMessage());
        }
    }
}