package com.farmorai.backend.service;


import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeMapper noticeMapper;


    public List<NoticeDto> getNoticeList() {
        return noticeMapper.getNoticeList();
    }

    @Transactional
    public void insertNotice(NoticeDto noticeDto) {
        noticeMapper.insertNotice(noticeDto);
    }

    @Transactional
    public void deleteNotice(Long notice_no) {
        noticeMapper.deleteNotice(notice_no);
    }



}
