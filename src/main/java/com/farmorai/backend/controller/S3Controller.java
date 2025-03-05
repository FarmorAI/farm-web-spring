package com.farmorai.backend.controller;

import com.farmorai.backend.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;


    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {

        String uploadFile = s3Service.uploadFile(file);
        return "file uploaded to S3: " + uploadFile;
    }


}
