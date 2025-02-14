package com.farmorai.backend.controller;


import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public List<NoticeDto> getNoticeList() {
        return noticeService.getNoticeList();
    }


    @PostMapping
    public Map<String,String> insertNotice(@RequestBody NoticeDto noticeDto) {
        noticeService.insertNotice(noticeDto);
        return Map.of("result","success");
    }

    @DeleteMapping("/{notice_no}")
    public Map<String,String> deleteNotice(@PathVariable Long notice_no) {
        noticeService.deleteNotice(notice_no);
        return Map.of("result","success");
    }





}
