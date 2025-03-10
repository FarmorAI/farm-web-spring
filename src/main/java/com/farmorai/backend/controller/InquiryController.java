package com.farmorai.backend.controller;

import com.farmorai.backend.dto.InquiryDto;
import com.farmorai.backend.dto.PageRequestDto;
import com.farmorai.backend.dto.PageResponseDto;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.securityFilter.jwt.JwtTokenProvider;
import com.farmorai.backend.service.InquiryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<InquiryDto>> getInquiryList(PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(inquiryService.getInquiryList(pageRequestDto));
    }

    @GetMapping("/{inquiryId}")
    public InquiryDto getInquiryById(@PathVariable Long inquiryId) {
        return inquiryService.getInquiryById(inquiryId);
    }

    @PostMapping
    public ResponseEntity<String> insertInquiry(
            @RequestBody InquiryDto inquiryDto,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest req
    ) {
        inquiryService.insertInquiry(inquiryDto, userDetails);
        return ResponseEntity.ok("Success");
    }

    @PutMapping
    public ResponseEntity<String> updateInquiry(@RequestBody InquiryDto inquiryDto) {
        inquiryService.updateInquiry(inquiryDto);
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<String> deleteInquiry(
            @PathVariable("inquiryId") Long inquiryId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if(userDetails == null) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }
        inquiryService.deleteInquiry(inquiryId);
        return ResponseEntity.ok("Success");
    }
}
