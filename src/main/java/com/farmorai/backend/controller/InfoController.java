package com.farmorai.backend.controller;

import com.farmorai.backend.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> searchBlogs(@RequestParam String query) {
        return infoService.getInfo(query);
    }
}
