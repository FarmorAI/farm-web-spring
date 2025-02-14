package com.farmorai.backend.controller;


import com.farmorai.backend.service.AIService;
import com.farmorai.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Log4j2
@RequiredArgsConstructor
public class AIController {


    private final FileUploadUtil fileUploadUtil;

    /**
     * 여러 개의 파일 업로드 API
     */
    @PostMapping("/upload")
    public Map<String,String> uploadFiles(@RequestBody List<MultipartFile> files) {

        fileUploadUtil.saveFiles(files);

        return Map.of("result", "success");

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