package com.farmorai.backend.controller;


import com.farmorai.backend.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Log4j2
@RequiredArgsConstructor
public class AIController {
    private final AIService aiService;

    @PostMapping("/detect")
    public ResponseEntity<byte[]> detectObjects(@RequestParam("file") MultipartFile file) {
        try {
            log.info(file);
            byte[] imageData = aiService.sendImage(file);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}