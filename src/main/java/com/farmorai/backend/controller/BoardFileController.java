package com.farmorai.backend.controller;

import com.farmorai.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/board/file")
public class BoardFileController {

    private final FileUploadUtil fileUploadUtil;

    @Value("${com.farmorai.upload.path}")
    private String uploadPath;

    /**
     * 게시판 파일 업로드 (다중 업로드 가능)
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFiles(@RequestPart("files") List<MultipartFile> files) {
        try {
            List<String> fileNames = new ArrayList<>();
            List<String> fileUrls = new ArrayList<>();

            if (files != null && !files.isEmpty()) {
                fileNames = fileUploadUtil.saveFiles(files);
                for (String fileName : fileNames) {
                    fileUrls.add("/api/board/file/download/" + fileName);
                }
            }

            Map<String, Object> response = Map.of(
                    "result", "success",
                    "uploadedFiles", fileNames,
                    "fileUrls", fileUrls
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("File upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("result", "fail", "message", e.getMessage()));
        }
    }

    /**
     * 파일 다운로드
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            log.error("File download error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 파일 삭제
     */
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadPath, fileName);
            Path thumbnailPath = Paths.get(uploadPath, "s_" + fileName);

            boolean fileDeleted = Files.deleteIfExists(filePath);
            boolean thumbDeleted = Files.deleteIfExists(thumbnailPath);

            if (!fileDeleted && !thumbDeleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "File not found"));
            }

            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));

        } catch (Exception e) {
            log.error("File delete failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "File delete failed"));
        }
    }
}
