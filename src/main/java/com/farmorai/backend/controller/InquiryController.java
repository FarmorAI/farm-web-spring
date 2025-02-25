package com.farmorai.backend.controller;

import com.farmorai.backend.dto.InquiryDto;
import com.farmorai.backend.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping
    public List<InquiryDto> getAllInquiry() {
        return inquiryService.getInquiryList();
    }

    @GetMapping("/{inquiryId}")
    public InquiryDto getInquiryById(@PathVariable Long inquiryId) {
        return inquiryService.getInquiryById(inquiryId);
    }
}
