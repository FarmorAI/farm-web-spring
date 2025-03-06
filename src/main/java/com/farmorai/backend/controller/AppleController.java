package com.farmorai.backend.controller;

import com.farmorai.backend.dto.AppleDto;
import com.farmorai.backend.service.AppleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apple")
public class AppleController {
    private final AppleService appleService;

    @GetMapping
    public List<AppleDto> getAppleList() {
        return appleService.getAppleList();
    }
}
