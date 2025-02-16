package com.farmorai.backend.controller;


import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.dto.PageRequestDto;
import com.farmorai.backend.dto.PageResponseDto;
import com.farmorai.backend.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        return noticeService.getNoticeDetail(noticeId);
    }


    @PostMapping
    public Map<String,String> insertNotice(@RequestBody NoticeDto noticeDto) {

        noticeService.insertNotice(noticeDto);
        return Map.of("result","success");
    }

    @PutMapping("/{noticeId}")
    public Map<String,String> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeDto noticeDto) {
        noticeService.updateNotice(noticeId, noticeDto);
        return Map.of("result","success");
    }

    @DeleteMapping("/{noticeId}")
    public Map<String,String> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return Map.of("result","success");
    }





}
