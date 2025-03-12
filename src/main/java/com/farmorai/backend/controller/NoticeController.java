package com.farmorai.backend.controller;


import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.dto.PageRequestDto;
import com.farmorai.backend.dto.PageResponseDto;

import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.NoticeService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


import static org.springframework.http.HttpStatus.*;



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<NoticeDto>> getNoticeList(PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(noticeService.getNoticeList(pageRequestDto));
    }

    @GetMapping("/{noticeId}")
    public NoticeDto getNoticeDetail(@PathVariable Long noticeId) {
        NoticeDto noticeDetail = noticeService.getNoticeDetail(noticeId);
        if(noticeDetail == null){
            throw new IllegalArgumentException("해당 공지사항이 존재하지 않습니다.");
        }
        return noticeDetail;
    }

    @PostMapping
    public ResponseEntity<?> insertNotice(@AuthenticationPrincipal CustomUserDetails userDetails, //로그인한 사용자 정보를 가져옵니다.
                                           @RequestBody NoticeDto noticeDto) {
        if(userDetails == null || !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ROLE_ADMIN"))){
            return ResponseEntity.status(FORBIDDEN).body("관리자만 글 등록이 가능합니다.");
        }
        log.info("noticeDto {}",noticeDto);
        noticeService.insertNotice(userDetails.getMemberId(),noticeDto);
        return ResponseEntity.ok("글 등록 성공");
    }


    @PutMapping("/{noticeId}")
    public Map<String,String> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeDto noticeDto) {
        noticeService.updateNotice(noticeId, noticeDto);
        return Map.of("result","success");
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> deleteNotice(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @PathVariable Long noticeId) {
        if(userDetails == null || !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ROLE_ADMIN"))){
            return ResponseEntity.status(FORBIDDEN).body("관리자만 공지사항을 삭제할 수 있습니다.");
        }
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok("공지사항이 삭제되었습니다.");
    }

}
