package com.farmorai.backend.controller;

import com.farmorai.backend.dto.InquiryDto;
import com.farmorai.backend.dto.PageRequestDto;
import com.farmorai.backend.dto.PageResponseDto;
import com.farmorai.backend.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<InquiryDto>> getInquiryList(PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(inquiryService.getInquiryList(pageRequestDto));
    }

    @GetMapping("/{inquiryId}")
    public InquiryDto getInquiryById(@PathVariable Long inquiryId) {
        return inquiryService.getInquiryById(inquiryId);
    }

    @PostMapping
    public ResponseEntity<String> insertInquiry(@RequestBody InquiryDto inquiryDto) {
        inquiryService.insertInquiry(inquiryDto);
        return ResponseEntity.ok("Success");
    }

    @PutMapping
    public ResponseEntity<String> updateInquiry(@RequestBody InquiryDto inquiryDto) {
        inquiryService.updateInquiry(inquiryDto);
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("/{inquiry}")
    public ResponseEntity<String> deleteInquiry(@PathVariable Long inquiry_id) {
        inquiryService.deleteInquiry(inquiry_id);
        return ResponseEntity.ok("Success");
    }
}
