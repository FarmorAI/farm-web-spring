package com.farmorai.backend.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class FileUploadUtil {

    @Value("${com.farmorai.upload.path}")
    private String uploadPath;


    /**
     * 여러 파일을 저장하고 저장된 파일명 목록을 반환
     */
    public List<String> saveFiles(List<MultipartFile> files) {
        List<String> savedFileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String fileName = saveFile(file);
            if (fileName != null) {
                savedFileNames.add(fileName);
            }
        }

        return savedFileNames;
    }

    /**
     * 단일 파일을 저장하고 저장된 파일명을 반환
     */
    public String saveFile(MultipartFile file) {
        try {
            // 디렉토리 존재 확인 및 생성
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // 고유한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // 파일 저장
            Path targetPath = Paths.get(uploadPath, uniqueFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 확장자를 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    /**
     * 파일을 다운로드할 수 있는 Resource와 ResponseEntity를 생성합니다.
     */
    public ResponseEntity<Resource> getFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new RuntimeException("잘못된 URL 경로입니다: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 업로드 디렉토리의 모든 파일 목록을 반환합니다.
     */
    public List<String> getFileList() {
        File folder = new File(uploadPath);
        String[] fileNames = folder.list();

        if (fileNames == null || fileNames.length == 0) {
            return List.of();
        }

        return Arrays.asList(fileNames);
    }

    /**
     * 지정된 파일들을 삭제합니다.
     */
    public void deleteFile(List<String> fileNames) {
        for (String fileName : fileNames) {
            Path filePath = Paths.get(uploadPath, fileName);

            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("파일 삭제 실패: " + fileName, e);
            }
        }
    }

    /**
     * 바이트 배열로부터 MultipartFile 객체를 생성합니다.
     */
    public MultipartFile createMultipartFile(byte[] content, String fileName) {
        String contentType = null;
        try {
            contentType = Files.probeContentType(Paths.get(fileName));
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return new MockMultipartFile(
                "file",
                fileName,
                contentType,
                content
        );
    }
}
