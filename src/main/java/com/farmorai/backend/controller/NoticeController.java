package com.farmorai.backend.controller;


import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.dto.PageRequestDto;
import com.farmorai.backend.dto.PageResponseDto;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
@Log4j2
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<NoticeDto>> getNoticeList(PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(noticeService.getNoticeList(pageRequestDto));
    }

    @GetMapping("/{noticeId}")
    public NoticeDto getNoticeDetail(@PathVariable Long noticeId) {
        return noticeService.getNoticeDetail(noticeId);
    }

    @PostMapping
    public ResponseEntity<?> insertNotice(@AuthenticationPrincipal CustomUserDetails userDetails, //로그인한 사용자 정보를 가져옵니다.
                                           @RequestBody NoticeDto noticeDto) {
        log.info(userDetails);
        log.info(SecurityContextHolder.getContext().getAuthentication());
        noticeService.insertNotice(userDetails.getMemberId(),noticeDto);
        return ResponseEntity.ok("글 등록 성공");
    }

    @PutMapping("/{noticeId}")
    public Map<String,String> updateNotice(@PathVariable Long noticeId,
                                           @RequestBody NoticeDto noticeDto) {
        noticeService.updateNotice(noticeId, noticeDto);
        return Map.of("result","success");
    }

    @DeleteMapping("/{noticeId}")
    public Map<String,String> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return Map.of("result","success");
    }
}
